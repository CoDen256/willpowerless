# nftables already installed, do not install iptables

# Side note
# add rule: nft add rule inet fw4 input ether saddr <addr> log prefix "Direct_Phone_TTL" level info
# add rule: nft add rule inet fw4 forward_lan ether saddr C2:F2:57:9C:EE:27 log prefix "Direct_Phone_TTL" level info
# add rule: nft add rule inet fw4 forward_lan ether saddr C2:F2:57:9C:EE:27 drop
# list rules with handle numbers:  nft -a list ruleset
# delete rule:  nft delete rule inet fw4 forward_lan handle 1347

uci add firewall rule # =cfg0e92bd
uci set firewall.@rule[-1].name='Drop'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci add_list firewall.@rule[-1].src_mac='C2:F2:57:9C:EE:27'
#uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci set firewall.@rule[-1].dest='wan'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].log='1'
uci commit firewall
/etc/init.d/firewall reload

# custom rules are done via specifiing nft snippets, added where
# same as add rule inet fw4 forward_lan ether saddr C2:F2:57:9C:EE:27 reject
echo "ether saddr C2:F2:57:9C:EE:27 reject" > /etc/custom_firewall.nft
#echo "add rule inet fw4 forward_lan ether saddr C2:F2:57:9C:EE:27 reject" > /etc/custom_firewall.nft, maybe also works

uci add firewall include
uci set firewall.@include[-1].type='nftables'
uci set firewall.@include[-1].path='/etc/custom_firewall.nft'
uci set firewall.@include[-1].chain='forward_lan'
uci set firewall.@include[-1].position='chain-pre'
uci commit firewall
cat /etc/config/firewall
/etc/init.d/firewall reload
nft -a list ruleset # ether saddr C2:F2:57:9C:EE:27 reject will be added in forward_lan chain at the very beginning

# only via include nft rules will be added, otherwise they will be removed on reload, if just added to the ruleset
#


#uci del firewall.@include[-1]
#uci del firewall.@rule[-1]

# TARGET CONFIG IS /etc/config/firewall
# it should include all the rules
# if custom rules needed, then add to firewall config


# uci -> firewall  (uses nft via 'include')
#
# uci helps with writing to /etc/config/firewall
# nftables apply actual rules in realtime, but are flushed upon restart