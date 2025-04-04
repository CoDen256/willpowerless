#!/usr/bin/env sh

# usage: /etc/judge/enable TOUCH_GRASS_ID 0

cat << "EOF" > /etc/judge/enable

#!/bin/sh -l

if [ $# -lt 2 ]; then
    echo "Usage: $0 VARIABLE_NAME ENABLE"
    exit 1
fi

var_name="$1"
value=""
enabled="$2"
eval "value=\"\$$var_name\""

echo "Setting $var_name ($value) to $enabled"

if [ -z "$value" ]; then
    echo "Error: $var_name not set!" >&2
    exit 1
fi
if [ "$enabled" != "0" ] && [ "$enabled" != "1" ]; then
    echo "Error: Second argument must be 0 or 1 (got: '$enabled')" >&2
    exit 1
fi

uci set firewall.$value.enabled="$enabled"
uci commit firewall && /etc/init.d/firewall reload

EOF
chmod 774 /etc/judge/enable