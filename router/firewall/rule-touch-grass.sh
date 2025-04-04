#!/usr/bin/env sh

uci add firewall rule
touch_grass=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)
uci set firewall.@rule[-1].name='Touch Grass'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='C2:F2:57:9C:EE:27'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci add_list firewall.@rule[-1].src_mac='0A:DF:73:F0:9D:8B'
uci add_list firewall.@rule[-1].src_mac='10:6F:D9:A0:1C:D1'
uci add_list firewall.@rule[-1].src_mac='00:C0:CA:AD:D0:23'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'
touch_grass=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload
echo -e "\nexport TOUCH_GRASS_ID=$touch_grass" >> /etc/profile && source /etc/profile