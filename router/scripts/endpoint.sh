#!/usr/bin/env sh

mkdir "/etc/judge"
echo "http://willpowerless-judge.up.railway.app/rulings/dev/openwrt" > /etc/judge/endpoint

chmod 664 /etc/judge/endpoint