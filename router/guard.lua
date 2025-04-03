#!/usr/bin/lua

local json = require("luci.json")
local io = require("io")

local endpoint = "http://willpowerless-judge.up.railway.app/firewall/rules"

local formatted_time = os.date("%Y-%m-%d %H:%M:%S")  -- ISO 8601 format
print("\n\n" .. formatted_time .. " - running " .. endpoint)

-- Fetch JSON from endpoint
local handle = io.popen(string.format("curl -s -f -L '%s'", endpoint:gsub("'", "'\\''")))
local response = handle:read("*a")
handle:close()

if not response or response == "" then
    io.stderr:write("HTTP request failed\n")
    os.exit(1)
end
print("Successfully requested")

-- Parse JSON
local ok, data = pcall(json.decode, response)
if not ok or type(data) ~= "table" then
    io.stderr:write("Invalid JSON response\n")
    os.exit(1)
end
print("Successfully parsed")

-- Process each rule (0-8)
for i = 0, 8 do
    local rule_key = tostring(i)
    print("\nParsing " .. rule_key .. " rule")
    local rule = data[rule_key]
    if not rule then break end  -- Stop if no more rules

    local uci_index = -1 - i  -- Converts "0" to -1, "1" to -2, etc.
    local uci_prefix = "firewall.@rule[" .. uci_index .. "]."
    print(uci_prefix)

    ---- Set simple key-value pairs (name, dest, target, enabled)
    --for _, key in ipairs({"name", "dest", "target", "enabled"}) do
    --    if rule[key] then
    --        os.execute(string.format(
    --                "uci set %s%s='%s'",
    --                uci_prefix,
    --                key,
    --                rule[key]:gsub("'", "'\\''")  -- Escape single quotes
    --        ))
    --    end
    --end
    --
    ---- Handle lists (e.g., src_mac)
    --if rule.src_mac and type(rule.src_mac) == "table" then
    --    os.execute("uci del " .. uci_prefix .. "src_mac 2>/dev/null")  -- Clear existing
    --    for _, mac in ipairs(rule.src_mac) do
    --        os.execute(string.format(
    --                "uci add_list %ssrc_mac='%s'",
    --                uci_prefix,
    --                mac:gsub("'", "'\\''")
    --        ))
    --    end
    --end
end

-- Commit and reload
--os.execute("uci commit firewall && /etc/init.d/firewall reload")
print("Firewall rules updated successfully")
os.exit(0)