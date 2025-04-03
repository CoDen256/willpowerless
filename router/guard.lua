#!/usr/bin/lua

local json = require("luci.json")
local io = require("io")

local PROPERTIES = {"name", "dest", "target", "enabled", "family", "proto"}  -- Single-value properties
local LIST_PROPERTIES = {"src_mac", "src_ip", "dest_ip", "dest_mac", "src_port", "dest_port"}  -- List properties

-- ======================
-- HTTP Fetch (using curl)
-- ======================
local function fetch_json(url)
    local handle = io.popen(string.format("curl -s -f -L '%s'", url:gsub("'", "'\\''")))
    local response = handle:read("*a")
    handle:close()
    return response
end
-- require("socket.http").request("http://willpowerless-judge.up.railway.app/firewall/rules")
-- require("luci.httpclient").request_to_buffer("https://willpowerless-judge.up.railway.app/firewall/rules")

-- ======================
-- Create and verify json
-- ======================
local function create_verify_json(json_data)
    local ok, data = pcall(json.decode, json_data)
    if not ok or type(data) ~= "table" then
        return nil, "Invalid JSON"
    end
    return data
end

-- ======================
-- Parse JSON & Build UCI Commands
-- ======================
local function process_rules(data)
    local commands = {}

    for i = 0, 8 do  -- Process rules 0-8
        local rule = data[tostring(i)]
        if not rule then break end

        local uci_prefix = string.format("firewall.@rule[%d].", -1 - i)
        print("Processing " .. tostring(i) .. " = " .. uci_prefix)

        -- Process non-list properties
        for _, key in ipairs(PROPERTIES) do
            if rule[key] and type(rule[key]) ~= "table" then
                table.insert(commands, string.format(
                        "uci set %s%s='%s'",
                        uci_prefix, key,
                        tostring(rule[key]):gsub("'", "'\\''")
                ))
            end
        end

        -- Process list properties
        for _, key in ipairs(LIST_PROPERTIES) do
            if rule[key] and type(rule[key]) == "table" then
                table.insert(commands, string.format(
                        "uci del %s%s 2>/dev/null", uci_prefix, key
                ))
                for _, val in ipairs(rule[key]) do
                    if val and val ~= "" then
                        table.insert(commands, string.format(
                                "uci add_list %s%s='%s'",
                                uci_prefix, key,
                                val:gsub("'", "'\\''")
                        ))
                    end
                end
            end
        end
    end

    -- Final commit if we made changes
    if #commands > 0 then
        table.insert(commands, "uci commit firewall")
        table.insert(commands, "/etc/init.d/firewall reload")
    end

    return commands
end


-- ======================
-- Main Execution
-- ======================

local formatted_time = os.date("%Y-%m-%d %H:%M:%S")

local endpoint = "http://willpowerless-judge.up.railway.app/firewall/rules"
print("\n\n" .. formatted_time .. " - running " .. endpoint)

local response = fetch_json(endpoint)

if not response or response == "" then
    io.stderr:write("HTTP request failed\n")
    os.exit(1)
end
print("Successfully requested")

local data, err = create_verify_json(response)
if not data then
    io.stderr:write("Error when verifying json: " .. err .. "\n")
    os.exit(1)
end
print("Successfully verified json")

local commands, err = process_rules(data)
if not commands then
    io.stderr:write("Error: " .. err .. "\n")
    os.exit(1)
end
print("Successfully processed")


for _, cmd in ipairs(commands) do
    print("Executing '" .. cmd .. "'")
end
-- Execute all commands sequentially
--for _, cmd in ipairs(commands) do
--    --os.execute(cmd)
--end

print("Firewall rules updated successfully")
os.exit(0)