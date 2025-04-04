#!/usr/bin/env sh

# hard control
uci add firewall rule
uci set firewall.@rule[-1].name='Lockdown'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci set firewall.@rule[-1].target='REJECT'

uci show firewall.@rule[-1]
uci commit firewall && /etc/init.d/firewall reload
