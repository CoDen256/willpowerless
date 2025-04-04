#!/usr/bin/env sh


uci add firewall rule
uci set firewall.@rule[-1].name='Sleepy time'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci add_list firewall.@rule[-1].src_mac='0A:DF:73:F0:9D:8B'
uci add_list firewall.@rule[-1].src_mac='72:33:6D:86:3E:BC'
uci add_list firewall.@rule[-1].stop_time='06:00:00'
uci add_list firewall.@rule[-1].start_time='22:30:00'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload