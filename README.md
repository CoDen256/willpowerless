# Judge
Judge Service: 
* /check - 200 if action is allowed
* aggregates the responses of witnesses by rules
* Rules
  * gym visit in last 5 days
  * or sick/on vacation
  * or within schedule
* deployed on a separatly from other projects free, easy-to-manage, hardly accessible platform
  * https://render.com
  * https://railway.com
* Accessible by:
  * Regulator Google Account

# Executors:
1. Master ISP Router (Vodafone Station)
* Connected to the ISP
* WPS inaccessible(physically impaired)
* SSH disabled
* Factory Reset
  * possible, but pointless, no factory password and no current password
* Hide: 
  * WiFi current password
  * Admin access current password
  * WiFi factory password
  * Admin factory password
* Access MUST be denied for (by MAC/by absence of a password)
  * Phone
  * PC
* Wireless Accessible by
  * Smart Home Devices
  * Printer
  * A's Devices
    * PC
    * Phone
    * Laptop
* LAN Accessible by
  * Guard Router
  * PC
  * Any other router
  * Laptop


2. Guard Router (TP-Link TL-WR940N v6)
* Installed an OpenWRT firmware (https://www.youtube.com/watch?v=IBVNX65K9KE)
* Connected to Master Router via Ethernet
* Acts as an AP or router
* WPS inaccessible
* SSH disabled
* Factory Reset disabled
* Wireless/LAN Accessible by anything/anyone
* Hide: 
  * Web Interface Password
* Cron job to check the judge, if not drop packets for everyone on the network

3. Guard App

## Setup
* [guard.sh](./guard.sh) containing the checker


* cron config for every 5 minute check


    */5 * * * * /guard.sh http://judge.service/check

* Disable factory reset button


    echo "" > /etc/rc.button/reset 

Extra setup
* Additional Google account for keeping all the hidden passwords

# Conclusion
* give: 
  * Vodafone Station WiFi current password
  * Vodafone Station Admin access current password

* hide:
  * Regulator Google account password
  * OpenWRT Web Interface Password
  * Vodafone Station WiFi factory password
  * Vodafone Station Admin factory password