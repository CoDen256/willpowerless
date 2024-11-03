#!/bin/sh

# Define variables
if [ -z "$1" ]; then
    echo "Usage: $0 <URL>"
    exit 1
fi
URL="$1"
# Make HTTP request and check if the response is successful
wget --spider "$URL"
RESPONSE=$?

# Check the response code
if [ "$RESPONSE" -ne 0 ]; then
    # Deny forwarding for the specific MAC address
#    iptables -I FORWARD -m mac --mac-source $MAC_ADDRESS -j DROP
    iptables -I FORWARD -j DROP
else
    # Remove any existing rule for the MAC address if the response is successful
#    iptables -D FORWARD -m mac --mac-source $MAC_ADDRESS -j DROP 2>/dev/null
    iptables -D FORWARD -j DROP 2>/dev/null
fi