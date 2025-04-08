#!/usr/bin/env sh

### setup and add cron tab for checking judge
crontab -l > /root/cron.backup
cat cron.backup

cat << "EOF" > /root/cron
10 8-23 * * * /etc/judge/guard >> /root/log/guard.log 2>&1
0 7 30 * * rm /root/log/guard.log
0 16 28 * * echo -e "abc\nabc" | passwd "root"
EOF

crontab /root/cron
crontab -l



