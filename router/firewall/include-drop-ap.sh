#!/usr/bin/env sh

# packets come with ttl generated either 256/128/64
# coming directly to the router ttl is down 1, thus 127 or 63,mac of the packet is the mac of the actual sender
# if the packets coming from AP to router, then ttl is 126 or 62,mac of the packet is just an AP, the actual sender one is behind the AP
# Deny any possible AP, to provide access via router on devices that are not subject to the rules for
# devices that are subject to the rules
# mac filtering is not required, will deny any AP by default

# TODO <limit rate 10/minute burst 1 packets> for logging only

uci add firewall include
echo  -e 'define ap_macs = { 72:33:6d:86:3e:bc, a0:ce:c8:5d:ba:8c, DC:A3:A2:08:14:56 }\nether saddr $ap_macs ip ttl {253, 254, 125, 126, 61, 62, 29, 30} counter packets 7542 bytes 456224 log prefix "Dropped from AP: " jump reject_to_wan comment "!fw4: ap-by-ttl-reject"' > /etc/drop-ttl.nft
# the same as: nft insert "rule inet fw4 forward_lan ip ttl 254 reject
cat /etc/drop-ttl.nft

uci set firewall.@include[-1].type='nftables'
uci set firewall.@include[-1].path='/etc/drop-ttl.nft'
uci set firewall.@include[-1].chain='forward'
uci set firewall.@include[-1].position='chain-pre'
uci commit firewall
cat /etc/config/firewall

/etc/init.d/firewall reload
nft -a list ruleset