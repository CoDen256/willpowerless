#!/bin/sh
echo ""
# Define variables
if [ -z "$1" ]; then
    echo "Usage: $0 <URL>"
    exit 1
fi
URL="$1"
# Make HTTP request and check if the response is successful
wget --spider --timeout=600 "$URL"
RESPONSE=$?
NOW=$( date '+%F_%H:%M:%S' )

echo $NOW
echo $RESPONSE
# Check the response code
if [ "$RESPONSE" -ne 0 ]; then
    uci set firewall.$SOFT_ID.enabled='1'
    uci set firewall.$HARD_ID.enabled='0'
    uci commit firewall && /etc/init.d/firewall reload
    /root/led.sh -1
else
    uci set firewall.$SOFT_ID.enabled='0'
    uci set firewall.$HARD_ID.enabled='0'
    uci commit firewall && /etc/init.d/firewall reload
    /root/led.sh 1
fi