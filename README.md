# Witnesses
Wellpass Witness
* verify the visited places

# Judges
Judge Service: 
    * /check - 200 if action is allowed
    * aggregates the responses of witnesses
    * deployed on a platform
    * Hide: 
      * Platform password

# Executors:
1. Master ISP Router (Vodafone Station)
* Connected to the ISP
* WPS inaccessible
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
* Connected to Master Router via Ethernet
* Acts as an AP or router
* WPS inaccessible
* SSH disabled
* Wireless/LAN Accessible by anything/anyone
* Hide: 
  * Web Interface Password
* Cron job to check the judge, if not drop packets for everyone on the network

Setup
* guard.sh containing the checker
* cron config for every 5 minute check


    */5 * * * * /guard.sh http://your-api-endpoint.com/check

  
