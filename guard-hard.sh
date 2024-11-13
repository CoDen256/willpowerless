#!/bin/sh -l
echo ""
NOW=$( date '+%F_%H:%M:%S' )
echo $NOW
# Define variables
if [ -z "$1" ]; then
    echo "Usage: $0 <URL>"
    exit 1
fi
URL="$1"
# Make HTTP request and check if the response is successful
wget --spider --timeout=600 "$URL"
RESPONSE=$?

echo $RESPONSE
echo "SOFT: $SOFT_ID, HARD: $HARD_ID"
# Check the response code
if [ "$RESPONSE" -ne 0 ]; then
    uci set firewall.$HARD_ID.enabled='1'
    uci commit firewall && /etc/init.d/firewall reload
    /root/led.sh 0
else
    uci set firewall.$HARD_ID.enabled='0'
    uci commit firewall && /etc/init.d/firewall reload
    echo "No punishment..."
fi