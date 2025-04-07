#!/usr/bin/env sh


uci add firewall rule
touch_grass_beamer=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)
uci set firewall.@rule[-1].name='Touch Grass (Beamer)'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='DC:A3:A2:08:14:56'
uci set firewall.@rule[-1].start_time='06:00:00'
uci set firewall.@rule[-1].stop_time='22:30:00'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload
echo -e "\nexport TOUCH_GRASS_BEAMER_ID=$touch_grass_beamer" >> /etc/profile && source /etc/profile
