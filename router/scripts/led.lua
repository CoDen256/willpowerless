#!/usr/bin/env lua

-- Constants
local COLORS = {
    GREEN = '\27[0;32m',
    ORANGE = '\27[0;33m',
    GREY = '\27[0;37m',
    NC = '\27[0m'
}

local LED_PATHS = {
    ORANGE = "/sys/class/leds/orange:wan/trigger",
    GREEN = "/sys/class/leds/green:wan/trigger"
}

-- LED control functions
local function set_led(led_path, value)
    local file = io.open(led_path, "w")
    if not file then
        io.stderr:write("Error: Could not open LED file " .. led_path .. "\n")
        os.exit(1)
    end
    file:write(value .. "\n")
    file:close()
end

local function set_leds(orange_value, green_value)
    set_led(LED_PATHS.ORANGE, orange_value)
    set_led(LED_PATHS.GREEN, green_value)
end

-- Display functions
local function print_status(message, color)
    print("Set " .. "wan" .. " -> " .. color .. message .. COLORS.NC)
end

-- Main logic
local function handle_led_state(value)
    if value == 1 then
        print_status("on", COLORS.GREEN)
        set_leds("none", "default-on")
    elseif value == -1 then
        print_status("off", COLORS.ORANGE)
        set_leds("default-on", "none")
    elseif value == 0 then
        print_status("disabled", COLORS.GREY)
        set_leds("none", "none")
    else
        io.stderr:write("Error: Invalid argument. Must be -1, 0, or 1\n")
        os.exit(1)
    end
end

-- Argument validation
local function validate_args()
    if #arg < 1 then
        io.stderr:write("Usage: " .. arg[0] .. " <-1|1|0>\n")
        os.exit(1)
    end

    local value = tonumber(arg[1])
    if not value or (value ~= -1 and value ~= 0 and value ~= 1) then
        io.stderr:write("Error: Argument must be -1, 0, or 1\n")
        os.exit(1)
    end

    return value
end

-- Main execution
local value = validate_args()
handle_led_state(value)