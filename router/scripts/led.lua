#!/usr/bin/env lua

-- Constants
local COLORS = {
    GREEN = '\27[0;32m',
    ORANGE = '\27[0;33m',
    GREY = '\27[0;37m',
    NC = '\27[0m'
}

-- LED control functions
local function set_led(led_path, value)
    local file = io.open(led_path, "w")
    if not file then
        return
    end
    file:write(value .. "\n")
    file:close()
end

-- Display functions
local function print_status(led, message, color)
    print("Set " .. led .. " -> " .. color .. message .. COLORS.NC)
end

-- Main logic
local function handle_led_state(value, led)
    local prefix = "/sys/class/leds/"
    local postfix =  ":".. led .. "/trigger"
    local green = prefix .. "green" .. postfix
    local orange = prefix .. "orange" .. postfix
    if value == 1 then
        print_status(green, "on", COLORS.GREEN)
        set_led(orange,  "none")
        set_led(green,  "default-on")
    elseif value == -1 then
        print_status(orange, "off", COLORS.ORANGE)
        set_led(green, "none")
        set_led(orange, "default-on")
    elseif value == 0 then
        print_status(green, "disabled", COLORS.GREY)
        set_led(green, "none")

        print_status(orange, "disabled", COLORS.GREY)
        set_led(orange, "none")
    else
        io.stderr:write("Error: Invalid argument. Must be -1, 0, or 1\n")
        os.exit(1)
    end
end

-- Argument validation
local function validate_args()
    if #arg ~= 2 then
        io.stderr:write("Usage: " .. arg[0] .. " <led> <-1|1|0>\n")
        os.exit(1)
    end

    local value = tonumber(arg[2])
    if not value or (value ~= -1 and value ~= 0 and value ~= 1) then
        io.stderr:write("Error: Argument must be -1, 0, or 1\n")
        os.exit(1)
    end

    return value, arg[1]
end

-- Main execution
local value, led = validate_args()
handle_led_state(value, led)