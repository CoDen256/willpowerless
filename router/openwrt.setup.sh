# tplink ax23
# install openwrt via uploading factory.bin to the router https://openwrt.org/toh/tp-link/archer_ax23_v1

opkg update

opkg install shadow-useradd
opkg install shadow-usermod
opkg install shadow-userdel
opkg install lua luci-lib-json luasocket luasec
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


##### add additional controls
# sleepy time
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

# restrict adb over wifi
uci add firewall rule
uci set firewall.@rule[-1].name='ADB over WiFi'
uci add_list firewall.@rule[-1].proto='all'
uci set firewall.@rule[-1].src='lan'
uci set firewall.@rule[-1].dest='lan'
uci add_list firewall.@rule[-1].src_mac='34:1C:F0:CD:FA:E8'
uci add_list firewall.@rule[-1].src_mac='0A:DF:73:F0:9D:8B'
uci set firewall.@rule[-1].target='REJECT'
uci set firewall.@rule[-1].enabled='0'

uci add firewall rule
uci add firewall rule
uci add firewall rule
uci add firewall rule
uci add firewall rule
uci add firewall rule

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
uci set firewall.$SOFT_ID.enabled='0'
uci set firewall.$HARD_ID.enabled='0'
uci commit firewall && /etc/init.d/firewall reload



#### add guard.sh and led.sh and guard-hard.sh
chmod +x /root/guard.sh
chmod +x /root/guard-hard.sh
chmod +x /root/led.sh
chmod +x /root/task-executor.sh


#### disable existing led green for wan
uci set system.led_wan.sysfs='green:wps'
uci del system.led_wan.mode
uci add_list system.led_wan.mode='link'
uci add_list system.led_wan.mode='tx'
uci add_list system.led_wan.mode='rx'

### setup and add cron tab for checking judge
crontab -l > cron
cat cron
# echo new cron into cron file

# every 60 minutes soft check
echo "10 8-23 * * * /root/guard.sh $CHECK_URL >> /root/guard.log 2>&1" >> cron
# hard check only every day at 9:14
echo "14 9 * * * /root/guard-hard.sh $CHECK_URL?hard=true >> /root/guard.hard.log 2>&1" >> cron
# cleanup every month on 30th
echo "0 7 30 * 1 rm /root/guard.log" >> cron
echo "0 7 30 * * rm /root/guard.hard.log" >> cron
# restore password to abc for maintenance window every month on 29th at 16:00
echo '0 16 29 * * echo -e "abc\nabc" | passwd "root"' >> cron
echo '*/1 * * * * /root/task-executor.sh >> /root/task.log 2>&1'
# install new cron file
crontab cron
crontab -l


### opendns
### copy resolv.conf
### check the id of dhcp config entry or do it via luci
cp /tmp/resolv.conf.d/resolv.conf.auto backup/resolv.conf.auto
uci del dhcp.cfg01411c.nonwildcard
uci del dhcp.cfg01411c.boguspriv
uci del dhcp.cfg01411c.filterwin2k
uci del dhcp.cfg01411c.filter_aaaa
uci del dhcp.cfg01411c.filter_a
uci del dhcp.cfg01411c.nonegcache
uci set dhcp.cfg01411c.resolvfile='/root/resolv.conf'
uci commit dhcp && /etc/init.d/dhcp reload

# set long current password
pass="stuff"
echo -e "$pass\n$pass" | passwd "root"
echo "$pass" | sha256sum | tee hash


# user setup


mkdir /home/diagnostics

# or verify that the /etc/passwd contains /bin/ash as the last column
useradd -r -s /bin/ash -d /home/diagnostics diagnostics
echo -e "abc\nabc" | passwd "diagnostics"

# chmod u-s /bin/busybox # DISABLE setuid BIT. IF diagnostics runs /bin/busybox, then everything is done via effective euid(0)==root
# shell and all tools like ls cat etc, are actually run effectively by root with this bit
# hopefully it won't crash any other service

# or verify thath the /etc/group contains
cat /etc/passwd
cat /etc/group
cat /etc/shadow

# setup the logs permissions, to able to view logs
chmod 666 /root/guard.log
chmod 666 /root/guard.hard.log

# check connected clients script
cat << "EOF" > /etc/show_wifi_clients.sh
#!/bin/sh

# /etc/show_wifi_clients.sh
# Shows MAC, IP address and any hostname info for all connected wifi devices
# written for openwrt 12.09 Attitude Adjustment

echo    "# All connected wifi devices, with IP address,"
echo    "# hostname (if available), and MAC address."
echo -e "# IP address\tname\tMAC address"
# list all wireless network interfaces
# (for universal driver; see wiki article for alternative commands)
for interface in `iwinfo | grep ESSID | cut -f 1 -s -d" "`
do
  # for each interface, get mac addresses of connected stations/clients
  maclist=`iwinfo $interface assoclist | grep dBm | cut -f 1 -s -d" "`
  # for each mac address in that list...
  for mac in $maclist
  do
    # If a DHCP lease has been given out by dnsmasq,
    # save it.
    ip="UNKN"
    host=""
    ip=`cat /tmp/dhcp.leases | cut -f 2,3,4 -s -d" " | grep -i $mac | cut -f 2 -s -d" "`
    host=`cat /tmp/dhcp.leases | cut -f 2,3,4 -s -d" " | grep -i $mac | cut -f 3 -s -d" "`
    # ... show the mac address:
    echo -e "$ip\t$host\t$mac"
  done
done
EOF
chmod +x /etc/show_wifi_clients.sh


cat << "EOF" > /etc/opkg-rm-pkg-deps.sh
#!/bin/sh
opkg update
URL="$(opkg --force-space --noaction install "${@}" \
| sed -n -e "/^Downloading\s*/s///p")"
rm -f /usr/lib/opkg/lock
for URL in ${URL}
do FILE="$(wget -q -O - "${URL}" \
| tar -O -x -z ./data.tar.gz \
| tar -t -z \
| sort -r \
| sed -e "s|^\.|/overlay/upper|")"
for FILE in ${FILE}
do if [ -f "${FILE}" ]
then rm -f "${FILE}"
elif [ -d "${FILE}" ]
then rmdir "${FILE}"
fi
done
done
EOF
chmod +x /etc/opkg-rm-pkg-deps.sh