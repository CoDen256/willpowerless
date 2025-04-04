#!/usr/bin/env sh

#### disable existing led green for wan
uci set system.led_wan.sysfs='green:wps'
uci del system.led_wan.mode
uci add_list system.led_wan.mode='link'
uci add_list system.led_wan.mode='tx'
uci add_list system.led_wan.mode='rx'
