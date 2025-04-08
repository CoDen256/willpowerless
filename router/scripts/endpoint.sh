#!/usr/bin/env sh

mkdir "/etc/judge"
echo "https://willpowerless-judge.up.railway.app/verdict" > /etc/judge/endpoint

chmod 664 /etc/judge/endpoint