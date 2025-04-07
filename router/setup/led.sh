#!/usr/bin/env sh

#### disable existing led green for wan and configure green wps button for it instead
uci set system.led_wan.sysfs='green:wps'
uci del system.led_wan.mode
uci add_list system.led_wan.mode='link'
uci add_list system.led_wan.mode='tx'
uci add_list system.led_wan.mode='rx'

cat << "EOF" > /etc/led
#!/bin/sh

GREEN='\033[0;32m'
ORANGE='\033[0;33m'
GREY='\033[0;37m'
NC='\033[0m'

# Check if an argument was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <-1|1|0>"
  exit 1
fi

if [ "$1" -eq 1 ]; then
  echo -e "Set on -> ${GREEN}green${NC}"
  echo "none" > /sys/class/leds/orange:wan/trigger
  echo "default-on" > /sys/class/leds/green:wan/trigger
elif [ "$1" -eq -1 ]; then
  echo -e "Set off -> ${ORANGE}orange${NC}"
  echo "default-on" > /sys/class/leds/orange:wan/trigger
  echo "none" > /sys/class/leds/green:wan/trigger
elif [ "$1" -eq 0 ]; then
  echo -e "Set disabled -> ${GREY}none${NC}"
  echo "none" > /sys/class/leds/orange:wan/trigger
  echo "none" > /sys/class/leds/green:wan/trigger
fi

EOF
chmod +x /etc/led