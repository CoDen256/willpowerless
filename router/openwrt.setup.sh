#!/usr/bin/env sh
# tplink ax23
# install openwrt via uploading factory.bin to the router https://openwrt.org/toh/tp-link/archer_ax23_v1

opkg update

opkg install shadow-useradd shadow-usermod shadow-userdel
opkg install lua luci-lib-json luasocket luasec
mkdir /root/backup
mkdir /root/log

####
# Luci -> wifi -> enable radio1 -> set password
# System -> general settings -> timezone

### disable buttons to avoid resetting to factory mode
./setup/disable-buttons.sh

### Apply rules
./firewall/rule-touch-grass.sh
./firewall/rule-touch-grass-beamer.sh

### DNS
./setup/dns.sh

### Add judge scripts and endpoint value to /etc/judge
./scripts/endpoint.sh
./scripts/enable.sh
./scripts/guard.lua.sh
./scripts/led.sh
./scripts/executor.sh

### Access setup
./setup/access # create proper password for root and save it
./setup/diagnostics.sh # add diagnostics user

# Cron
./setup/cron.sh

# add extra scripts to /etc/
./scripts/clients.sh