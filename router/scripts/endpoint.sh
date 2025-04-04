#!/usr/bin/env sh

mkdir "/etc/judge"
cat << "EOF" > /etc/judge/endpoint
http://willpowerless-judge.up.railway.app/verdict
EOF

chmod 664 /etc/judge/endpoint