#!/usr/bin/env sh

### opendns
### copy ./resolv.conf
### check the id of dhcp config entry or do it via luci
cp /tmp/resolv.conf.d/resolv.conf.auto /root/backup/resolv.conf.auto

uci del dhcp.cfg01411c.nonwildcard
uci del dhcp.cfg01411c.boguspriv
uci del dhcp.cfg01411c.filterwin2k
uci del dhcp.cfg01411c.filter_aaaa
uci del dhcp.cfg01411c.filter_a
uci del dhcp.cfg01411c.nonegcache
uci set dhcp.cfg01411c.resolvfile='/root/resolv.conf'
uci commit dhcp && /etc/init.d/dhcp reload


cat << "EOF" > /root/resolv.conf

nameserver 208.67.222.123
nameserver 208.67.220.123

# or
# 208.67.222.222
# 208.67.220.220


# default
# nameserver 8.8.8.8
# nameserver 8.8.4.4

EOF

chmod 664 /root/resolv.conf