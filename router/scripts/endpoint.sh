#!/usr/bin/env sh

mkdir "/etc/judge"
echo "http://localhost/rulings/dev/openwrt" > /etc/judge/endpoint

chmod 664 /etc/judge/endpoint