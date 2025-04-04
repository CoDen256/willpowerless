#!/usr/bin/env sh

### setup and add cron tab for checking judge
crontab -l > cron
cat cron
# echo new cron into cron file

# every 60 minutes soft check
echo "10 8-23 * * * /root/guard.sh $CHECK_URL >> /root/guard.log 2>&1" >> cron
# hard check only every day at 9:14
echo "14 9 * * * /root/guard-hard.sh $CHECK_URL?hard=true >> /root/guard.hard.log 2>&1" >> cron
# cleanup every month on 30th
echo "0 7 30 * 1 rm /root/guard.log" >> cron
echo "0 7 30 * * rm /root/guard.hard.log" >> cron
# restore password to abc for maintenance window every month on 29th at 16:00
echo '0 16 29 * * echo -e "abc\nabc" | passwd "root"' >> cron
echo '*/1 * * * * /root/task-executor.sh >> /root/task.log 2>&1'
# install new cron file
crontab cron
crontab -l


