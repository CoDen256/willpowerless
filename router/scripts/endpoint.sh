#!/usr/bin/env sh

mkdir "/etc/judge"
echo "http://217.154.222.188/rulings/dev/openwrt" > /etc/judge/endpoint

chmod 664 /etc/judge/endpoint