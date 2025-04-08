#!/usr/bin/env sh

### setup and add cron tab for checking judge
crontab -l > cron
cat cron
# echo new cron into cron file

# every 60 minutes soft check
echo "10 8-23 * * * /root/guard.sh $CHECK_URL >> /root/guard.log 2>&1" >> cron
# hard check only every day at 9:14
echo "0 7 30 * 1 rm /root/guard.log" >> cron
# restore password to abc for maintenance window every month on 29th at 16:00
echo '0 16 28 * * echo -e "abc\nabc" | passwd "root"' >> cron
# install new cron file
crontab /root/cron
crontab -l

cat << "EOF" >> /root/cron

nameserver 208.67.222.123
nameserver 208.67.220.123

# or
# 208.67.222.222
# 208.67.220.220


# default
# nameserver 8.8.8.8
# nameserver 8.8.4.4

EOF

