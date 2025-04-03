#!/bin/sh

# Check if an argument was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <-1|1|0>"
  exit 1
fi

if [ "$1" -eq 1 ]; then
  echo "Set on -> green"
  echo "none" > /sys/class/leds/orange:wan/trigger
  echo "default-on" > /sys/class/leds/green:wan/trigger
elif [ "$1" -eq -1 ]; then
  echo "Set off -> orange"
  echo "default-on" > /sys/class/leds/orange:wan/trigger
  echo "none" > /sys/class/leds/green:wan/trigger
else
  echo "Set disabled -> none"
  echo "none" > /sys/class/leds/orange:wan/trigger
  echo "none" > /sys/class/leds/green:wan/trigger
fi