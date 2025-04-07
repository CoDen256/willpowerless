#!/usr/bin/env sh

cat << "EOF" > /etc/judge/guard
#!/usr/bin/lua

local http = require("socket.http")
local io = require("io")

local function read_file_to_string(file_path)
    local file = io.open(file_path, "r")  -- Open in read mode
    if not file then
        return nil, "Could not open file: " .. file_path
    end

    local content = file:read("*a")  -- Read all content
    file:close()
    return content
end

local function enable_rule(rule, enable)
    os.execute("/etc/judge/enable " .. rule .. " " .. tostring(tonumber(enable)))
end

local function set_led(value)
    os.execute("/etc/led " .. tostring(tonumber(value)))
end

local function get_endpoint()
    return read_file_to_string("/etc/judge/endpoint")
end

local function get_verdict(url)
    local body, code, headers = http.request(url)
    if (not tonumber(code) or tonumber(code)  > 399) then
        print("Error when requesting verdict: " .. code .. "\n" .. body)
        return false
    end
    return true
end

-- ======================
-- Main Execution
-- ======================

local formatted_time = os.date("%Y-%m-%d %H:%M:%S")

local endpoint = get_endpoint()
print("\n\n" .. formatted_time .. " - running " .. endpoint)

if not get_verdict(endpoint) then
    enable_rule("TOUCH_GRASS_ID", 1)
    enable_rule("TOUCH_GRASS_BEAMER_ID", 1)
    set_led(-1)
else
    enable_rule("TOUCH_GRASS_ID", 0)
    enable_rule("TOUCH_GRASS_BEAMER_ID", 0)
    set_led(1)
end

EOF
chmod +x /etc/judge/guard
chmod u+s /etc/judge/guard