# tplink ax23
# install openwrt via uploading factory.bin to the router https://openwrt.org/toh/tp-link/archer_ax23_v1

opkg update
####
# Luci -> wifi -> enable radio1 -> set password
# System -> general settings -> timezone

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


# TODO potentially failsafe mode is possible-> disable


#### add impulse control traffic
# hard control
uci add firewall rule
uci set firewall.@rule[-1].name='Impulse Control (Hard)'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'
hard_id=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)

# soft control
uci add firewall rule
uci set firewall.@rule[-1].name='Impulse Control (Soft)'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='wan'
uci add_list firewall.@rule[-1].src_mac='C2:F2:57:9C:EE:27'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci add_list firewall.@rule[-1].src_mac='0A:DF:73:F0:9D:8B'
uci add_list firewall.@rule[-1].src_mac='00:C0:CA:AD:D0:23'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'
soft_id=$(uci show firewall.@rule[-1] | head -n1 | cut -d'.' -f2 | cut -d'=' -f1)

uci commit firewall
/etc/init.d/firewall reload

echo "" >> /etc/profile
echo "export HARD_ID=$hard_id" >> /etc/profile
echo "" >> /etc/profile
echo "export SOFT_ID=$soft_id" >> /etc/profile
echo "" >> /etc/profile
echo "export CHECK_URL=https://impulse-judge-service.onrender.com/check" >> /etc/profile
echo "" >> /etc/profile
source /etc/profile



# use to disable/enable
uci set firewall.$SOFT_ID.enabled='1'
uci set firewall.$HARD_ID.enabled='0'
uci commit firewall && /etc/init.d/firewall reload



#### add guard.sh and led.sh and guard-hard.sh
chmod +x /root/guard.sh
chmod +x /root/guard-hard.sh
chmod +x /root/led.sh


#### disable existing led green for wan
uci set system.led_wan.sysfs='green:wps'
uci del system.led_wan.mode
uci add_list system.led_wan.mode='link'
uci add_list system.led_wan.mode='tx'
uci add_list system.led_wan.mode='rx'

### setup and add cron tab for checking judge
crontab -l > cr
cat cr
# echo new cron into cron file
# every 30 minutes
echo "*/30 8-23 * * * /root/guard.sh $CHECK_URL >> /root/guard.log 2>&1" >> cr
# hard check only every day at 9:20
echo "20 9 * * * /root/guard-hard.sh $CHECK_URL?hard=true >> /root/guard.hard.log 2>&1" >> cr
# cleanup
echo "0 7 * * 1 rm /root/guard.log" >> cr
echo "0 7 */10 * * rm /root/guard.hard.log" >> cr
# install new cron file
crontab cr
rm cr
crontab -l


### opendns
### copy resolv.conf
cp /tmp/resolv.conf.d/resolv.conf.auto backup/resolv.conf.auto
uci del dhcp.cfg01411c.nonwildcard
uci del dhcp.cfg01411c.boguspriv
uci del dhcp.cfg01411c.filterwin2k
uci del dhcp.cfg01411c.filter_aaaa
uci del dhcp.cfg01411c.filter_a
uci del dhcp.cfg01411c.nonegcache
uci set dhcp.cfg01411c.resolvfile='/root/resolv.conf'
uci commit dhcp && /etc/init.d/dhcp reload