#!/usr/bin/env lua

local FILE_PATH = "/etc/judge/task"
local LOG_PATH = "/root/guard.log"
local HARD_LOG_PATH = "/root/guard.hard.log"

local function file_exists(path)
    local f = io.open(path, "r")
    if f then
        f:close()
        return true
    end
    return false
end

local function execute_checks()
    -- Get current timestamp
    local now = os.date("%Y-%m-%d_%H:%M:%S")
    print("")
    print(now .. ": " .. FILE_PATH)
    print("File exists. Executing code...")
    print("Checking")

    -- Execute guard.sh and redirect output
    local check_url = os.getenv("CHECK_URL") or ""
    os.execute("/root/guard.sh " .. check_url .. " >> " .. LOG_PATH .. " 2>&1")

    print("Checking hard")
    -- Execute guard-hard.sh and redirect output
    os.execute("/root/guard-hard.sh " .. check_url .. "?hard=true >> " .. HARD_LOG_PATH .. " 2>&1")

    -- Delete the trigger file
    os.remove(FILE_PATH)
    print("File deleted.")
end

-- Main execution
if file_exists(FILE_PATH) then
    execute_checks()
end