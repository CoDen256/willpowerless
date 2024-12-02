#!/bin/sh -l
echo ""
NOW=$( date '+%F_%H:%M:%S' )
echo $NOW
# Define variables
if [ -z "$1" ]; then
    echo "Usage: $0 <URL>"
    exit 1
fi

wget --spider -q --timeout=60 "http://google.com"
if [ $? -eq 0 ]; then
  echo "We're online"
else
  echo "We're offline, ignoring outcome"
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

    echo "Checking again"
    # check again
    wget --spider --timeout=600 "$URL"
    RESPONSE_AGAIN=$?

    if [ "$RESPONSE_AGAIN" -ne 0 ]; then
      uci set firewall.$SOFT_ID.enabled='1'
      uci commit firewall && /etc/init.d/firewall reload
      /root/led.sh -1
    else
      uci set firewall.$SOFT_ID.enabled='0'
      uci commit firewall && /etc/init.d/firewall reload
      /root/led.sh 1
    fi

else
    uci set firewall.$SOFT_ID.enabled='0'
    uci commit firewall && /etc/init.d/firewall reload
    /root/led.sh 1
fi