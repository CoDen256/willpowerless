#!/usr/bin/env sh


uci add firewall rule
openwrt_access_ruling=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)
uci set firewall.@rule[-1].name='Judge Openwrt Access Ruling'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload
echo -e "\nexport RULE_OPENWRT_ACCESS_RULING=$openwrt_access_ruling" >> /etc/profile && source /etc/profile
