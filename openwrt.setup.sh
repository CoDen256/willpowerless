# tplink ax23
# install openwrt via uploading factory.bin to the router https://openwrt.org/toh/tp-link/archer_ax23_v1

opkg update
####
# Luci -> wifi -> enable radio1 -> set password


########

# packets come with ttl generated either 256/128/64
# coming directly to the router ttl is down 1, thus 127 or 63,mac of the packet is the mac of the actual sender
# if the packets coming from AP to router, then ttl is 126 or 62,mac of the packet is just an AP, the actual sender one is behind the AP
# Deny any possible AP, to provide access via router on devices that are not subject to the rules for
# devices that are subject to the rules
# mac filtering is not required, will deny any AP by default

# TODO <limit rate 10/minute burst 1 packets> for logging only

uci add firewall include

echo 'ip ttl {254, 126, 62, 30} counter packets 7542 bytes 456224 log prefix "Dropped from AP: " jump reject_to_wan comment "!fw4: ap-by-ttl-reject"' > /etc/custom_firewall.nft # the same as: nft insert "rule inet fw4 forward_lan ip ttl 254 reject
cat /etc/custom_firewall.nft

uci set firewall.@include[-1].type='nftables'
uci set firewall.@include[-1].path='/etc/custom_firewall.nft'
uci set firewall.@include[-1].chain='forward'
uci set firewall.@include[-1].position='chain-pre'
uci commit firewall
cat /etc/config/firewall
/etc/init.d/firewall reload
nft -a list ruleset

### disable buttons
mkdir backup
cp -r /etc/rc.button/ ./backup/rc.button

for f in /etc/rc.button/*; do printf "#!/bin/sh\necho 'executing $f'> /root/buttons.log" > "$f"; done


### TODO potentially failsafe mode is possible-> disable



#### add led.sh



#### add impulse control traffic
# hard control
uci add firewall rule
uci set firewall.@rule[-1].name='Impulse Control (Hard)'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='*'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='E8:9C:25:43:3F:DB'
uci add_list firewall.@rule[-1].src_mac='9C:53:22:35:10:40'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'
hard_id=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)

# soft control
uci add firewall rule
uci set firewall.@rule[-1].name='Impulse Control (Soft)'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='*'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='C2:F2:57:9C:EE:27'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'
soft_id=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)

uci commit firewall
/etc/init.d/firewall reload

echo "export HARD_ID=$hard_id" >> /etc/profile
echo "export SOFT_ID=$soft_id" >> /etc/profile
source /etc/profile