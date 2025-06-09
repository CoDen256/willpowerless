#!/usr/bin/env sh
# tplink ax23
# install openwrt via uploading factory.bin to the router https://openwrt.org/toh/tp-link/archer_ax23_v1

opkg update

opkg install shadow-useradd shadow-usermod shadow-userdel
opkg install lua luci-lib-json luasocket luasec
opkg install file # to check system arch
opkg install openssh-sftp-server

mkdir /root/backup
mkdir /root/log

####
# Luci -> wifi -> enable radio1 -> set password
# System -> general settings -> timezone

### Apply rules

./firewall/include-drop-ap.sh # drop packets from Access Points to prevent sharing internet # TODO config dropping AP from judge
./firewall/rule-judge.sh
./firewall/rule-touch-grass.sh
./firewall/rule-touch-grass-beamer.sh

### DNS
./setup/dns.sh

### Add judge scripts and endpoint value to /etc/judge
./scripts/endpoint.sh
./scripts/enable.sh
./scripts/guard.lua.sh

### Access setup
./setup/access.sh # create proper password for root and save it
./setup/diagnostics.sh # add diagnostics user

### disable buttons to avoid resetting to factory mode
./setup/disable-buttons.sh

# Cron
./setup/cron.sh # TODO resetting config from judge

# add extra scripts to /etc/
./scripts/led.lua.sh
./scripts/clients.sh