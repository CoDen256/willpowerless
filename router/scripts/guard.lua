#!/usr/bin/lua

local http = require("socket.http")
local io = require("io")
local json = require("luci.json")
local jsonc = require("luci.jsonc")

function trim(s)
    return (string.gsub(s, "^%s*(.-)%s*$", "%1"))
end

local function read_file_to_string(file_path)
    local file = io.open(file_path, "r")  -- Open in read mode
    if not file then
        return nil, "Could not open file: " .. file_path
    end

    local content = file:read("*a")  -- Read all content
    file:close()
    return tostring(content)
end

local function enable_rule(rule, enable)
    os.execute("/etc/judge/enable " .. rule .. " " .. tostring(tonumber(enable)))
end

local function set_led(value)
    os.execute("/etc/led " .. tostring(tonumber(value)))
end

local function set_mac(rule, macs)
    if #macs == 0 then
        set_led(1)
    else
        set_led(-1)
    end
    local mac_list = table.concat(macs, " ")
    os.execute("/etc/judge/set_mac " .. rule .. " " .. mac_list)
end

local function run_lockdown()
    enable_rule("RULE_TOUCH_GRASS", 1)
    enable_rule("RULE_TOUCH_GRASS_BEAMER", 1)
    set_led(0)
end

local function un_lockdown()
    enable_rule("RULE_TOUCH_GRASS", 0)
    enable_rule("RULE_TOUCH_GRASS_BEAMER", 0)
    set_led(1)
end


local function get_endpoint()
    return trim(read_file_to_string("/etc/judge/endpoint"))
end

local function create_verify_json(json_data)
    local ok, data = pcall(json.decode, json_data)
    if not ok or type(data) ~= "table" then
        return data, "Invalid JSON"
    end
    return data
end

local function request(url)
    local body, code, headers = http.request(tostring(url))
    if (not tonumber(code)) then
        print("Cannot request " .. url .. " : " .. tostring(code) .. "\n" .. tostring(body))
        return false
    end

    if (tonumber(code) > 399) then
        print("Error when requesting: " .. code .. "\n" .. tostring(body))
        return false, code
    end

    local parsed, err = create_verify_json(body)
    if (err) then
        print("Error when parsing json: " .. err .. ":\n" .. tostring(parsed))
        return false, code
    end

    return true, code, parsed
end

local function extract_blocked_macs(json_data)
    local blocked_macs = {}

    for mac, data in pairs(json_data) do
        if (data.ruling) then
            print(mac .. " -> " .. data.ruling.action)
        end
        -- Validate MAC format and check for BLOCK action
        if mac:match("^[0-9A-Fa-f][0-9A-Fa-f]:[0-9A-Fa-f][0-9A-Fa-f]:[0-9A-Fa-f][0-9A-Fa-f]:[0-9A-Fa-f][0-9A-Fa-f]:[0-9A-Fa-f][0-9A-Fa-f]:[0-9A-Fa-f][0-9A-Fa-f]$") and
                data.ruling and data.ruling.action == "BLOCK" then
            table.insert(blocked_macs, mac)
        end
    end

    return blocked_macs
end

local function write_file(path, content)
    local dir = path:match("^(.*)/")
    if dir and nixio.fs.stat(dir, "type") ~= "dir" then
        print("Directory doesn't exist")
        return false
    end

    local file = io.open(path, "w")
    if not file then
        print("No write permission or other error")
        return false
    end

    file:write(content)
    file:close()
    return true
end

local function check_rulings(json_data)
    local ACCESS_RULE = "RULE_OPENWRT_ACCESS_RULING"
    local target = json_data.access

    print("Checking judge rulings for " .. ACCESS_RULE .. "\n" .. tostring(jsonc.stringify(target)))
    if (not target) then
        print("No access rulings, abort")
        return
    end

    local macs = extract_blocked_macs(target)
    print("Blocking (" .. table.concat(macs, ",") .. ")")
    set_mac(ACCESS_RULE, macs)
end

local function check_dns(json_data)
    local target = json_data.dns

    print("Checking judge rulings for dns \n" .. tostring(jsonc.stringify(target)))
    if (not target) then
        print("No dns rulings, abort")
        return
    end

    local content = ""
    for dns, data in pairs(target) do
        if (data.ruling) then
            print(dns .. " -> " .. data.ruling.action)
        end
        if data.ruling and data.ruling.action == "FORCE" then
            content = content .. dns .. "\n"
        end
        if data.ruling and data.ruling.action == "BLOCK" then
            content = content .. "#" .. dns .. "\n"
        end
    end

    print("DNS: \n" .. content)

    write_file("/root/resolv.conf", content)
end

-- ======================
-- Main Execution
-- ======================

local formatted_time = os.date("%Y-%m-%d %H:%M:%S")
local endpoint = get_endpoint()

print("\n\n" .. formatted_time .. " - running " .. tostring(endpoint))
local ok, code, data = request(endpoint)
if (not code) then
    print("Running Lockdown since I cannot request judge")
    run_lockdown()
    os.exit(1)
end
un_lockdown()

if (not ok) then
    print("Judge said something funky, ignoring for now any rules, not doing anything, removing lockdown")
    os.exit(1)
end

if (not data) then
    print("Judge sent strange data, ignoring for now any rules")
    os.exit(1)
end
check_rulings(data)
check_dns(data)