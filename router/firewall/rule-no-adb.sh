#!/usr/bin/env sh

uci add firewall rule
uci set firewall.@rule[-1].name='Forbid ADB over WiFi'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='lan'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci add_list firewall.@rule[-1].src_mac='0A:DF:73:F0:9D:8B'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload