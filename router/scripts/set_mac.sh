#!/usr/bin/env sh

# usage: /etc/judge/set_mac RULE_OPENWRT_ACCESS_RULING ...

cat << "EOF" > /etc/judge/set_mac
#!/bin/sh -l

if [ $# -lt 1 ]; then
    echo "Usage: $0 (RULE_OPENWRT_ACCESS_RULING) [MAC_ADDR0, MAC_ADDR1, ...]"
    exit 1
fi

var_name="$1"
value=""
eval "value=\"\$$var_name\""
if [ -z "$value" ]; then
    echo "Error: $var_name not set!" >&2
    exit 1
fi

shift

uci del firewall.$value.src_mac

echo -e "[$value] Removed all macs. Adding following MAC addresses to $var_name:\n[$value] $@"

VALID_MACS=0
# Add each MAC address to the rule
for mac in "$@"; do

    if echo "$mac" | grep -qE '^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$'; then # Basic MAC address format validation (xx:xx:xx:xx:xx:xx)
        echo "[$value] Adding $mac "
        uci add_list firewall.$value.src_mac="$mac"
        VALID_MACS=$((VALID_MACS + 1))
    else
        echo "[$value] Warning: '$mac' doesn't look like a valid MAC address" >&2
    fi
done

if [ $VALID_MACS -gt 0 ]; then
    uci set firewall.$value.enabled=1
    echo "[$value] Enabled rule with $VALID_MACS MAC address(es)"
else
    uci set firewall.$value.enabled=0
    echo "[$value] Disabled rule - no valid MAC addresses provided"
fi

#uci show firewall.$value
uci commit firewall && /etc/init.d/firewall reload

EOF
chmod 774 /etc/judge/set_mac