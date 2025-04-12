
openwrt-guard :

`GET /devices/*/networks/openwrt`
`GET /devices/deimos/networks/openwrt`
->
```
200/403

{
    "ruling": {
        "action": "BLOCKED"
        "from": ""
        "to": ""
        "reason" : {
            ... no gym or not sick or schedule ...
        }
    }

}
```
-> block * devices (deimos, mi, etc)

---
`GET /devices/deimos/networks/`
```
200/403

{

    "openwrt" : {
    
    }
    "ruling": {
        "action": "BLOCKED"
        "from": ""
        "to": ""
        "reason" : {
            ... no gym or not sick or schedule ...
        }
    }

}
```

---
android-guard:

`GET /devices/mi/networks/*`
->
```
200/403

{
    "ruling": {
        "action": "BLOCKED"
        "from": ""
        "to": ""
        "reason"
    }


}
```

---
Gym Law{
    condition: () -> Boolean,
    rulings: [{
        "path": "/devices/*/networks/* "
        "action": "BLOCKED"
        "from" : ""
        "to"
    }]    
}

Sick Law{
    condition: () -> Boolean,
        rulings: [{
        "path": "/devices/*/networks/* "
        "action": "FORCE"
        "from" : ""
        "to"
        }]

}
->

Law = {
    rulings: [{
    "path": "/devices/*/networks/* "
    "action": "ALLOW"
    "from" : ""
    "to"
    }]
}

```json

[
  {
    "path": "/openwrt/devices/*",
    "action": "BLOCK"
  },
  {
    "path": "/openwrt/devices/*",
    "action": "BLOCK"
  },
  
]

```

openwrt
`GET /devices/*/network/openwrt`

403
```json
{
  "ruling": {
    "action": "BLOCK"
  }
}
```

`GET /devices/*/network/openwrt`

403
```json
{
  "ruling": {
    "action": "BLOCK"
  }
}
```

---
android
`GET /devices/mi/apps`

200
```json
{
  "*telegram*": {
    "ruling": {
      "action": "BLOCK"
    }
  },
  "telegram.beta": {
    "ruling": {
      "action": "FORCE"
    }
  },
  "vpn.rethink": {
    "ruling": {
      "action": "FORCE"
    }
  }
}
```

`GET /devices/mi/dns`

200
```json
{
  "rethink.dns/bla" : {
    "ruling": {
      "action": "FORCE"
    }
  } 
}
```

`GET /devices/mi/dns`

200
```json
{
  "rethink.dns/bla" : {
    "ruling": {
      "action": "FORCE"
    }
  } 
}
```

`GET /`

```json
{
  "devices": {
    "mi": {
      "networks" : {
        "openwrt" : {
          
        },
        "sim.de" : {
          
        },
        "*" : {
          
        }
      },
      
      "dns" : {
        
      }
    },

    "*": {
      "networks" : {
        "openwrt" : {

        }
      }
    }
  } 
}
```

`GET /rulings`
---
```json
[
  {
    "path": "/devices/*/network/openwrt",
    "action": "BLOCK",
    "reason": "no gym"
  },

  {
    "path": "/devices/*/network/openwrt",
    "action": "FORCE",
    "reason": "sickness"
  },

  {
    "path": "/devices/*/network/openwrt/dns/opendns.com",
    "action": "FORCE",
    "reason": "safe search"
  },
  
  {
    "path": "/devices/mi/network/*/dns/rethinkblabla.com",
    "action": "FORCE",
    "reason": "safe search and no social"
  },

  {
    "path": "/devices/mi/apps/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social"
  },
  {
    "path": "/devices/mi/domains/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social via website"
  },

  {
    "path": "/devices/mi/domains/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social via website"
  },

  {
    "path": "/devices/mi/apps/*telegram*",
    "action": "BLOCK",
    "reason": "no normal telegram"
  },

  {
    "path": "/devices/mi/apps/beta.telegram",
    "action": "FORCE",
    "reason": "no channels telegram"
  },

  {
    "path": "/devices/mi/apps/vpn.rethink",
    "action": "FORCE",
    "reason": "rethink must have"
  },

  {
    "path": "/devices/mi/network/sim.de",
    "action": "BLOCK",
    "reason": "budgeted use of the mobile data"
  },

  {
    "path": "/devices/mi/network/*",
    "action": "BLOCK",
    "reason": "no gym block any internet connection?"
  }
]
```
by default: ALLOW
ruling can only be BLOCK OR FORCE
the laws either add a ruling if condition fulfills or add nothing

? what about the budget, it just allows not to force use vpn,
domain rules stay the same? because they are for the vpn, which will not be used?
what if we are not using vpn to block them, but we want to allow budgeted use
them, semantically we should allow domains as rulings and not just allow vpn
? combining
"/devices/*/network/openwrt/dns/opendns.com"
and
"/devices/mi/network/*/dns/rethinkblabla.com"

what should result this in

---
Current
`GET /rulings`

```json
[
  {
    "path": "/openwrt/devices/abcd",
    "action": "BLOCK",
    "reason": "no gym"
  },

  {
    "path": "/openwrt/devices/abcd",
    "action": "FORCE",
    "reason": "sickness"
  },

  {
    "path": "/openwrt/dns/opendns.com",
    "action": "FORCE",
    "reason": "safe search"
  },
  
  {
    "path": "/mi/dns/rethinkblabla.com",
    "action": "FORCE",
    "reason": "safe search and no social"
  },

  {
    "path": "/mi/apps/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social"
  },
  {
    "path": "/mi/domains/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social via website"
  },

  {
    "path": "/mi/domains/*reddit|*instagram",
    "action": "BLOCK",
    "reason": "no social via website"
  },

  {
    "path": "/mi/apps/*telegram*",
    "action": "BLOCK",
    "reason": "no normal telegram"
  },

  {
    "path": "/mi/apps/beta.telegram",
    "action": "FORCE",
    "reason": "no channels telegram"
  },

  {
    "path": "/mi/apps/vpn.rethink",
    "action": "FORCE",
    "reason": "rethink must have"
  },

  {
    "path": "/mi/networks/sim.de",
    "action": "BLOCK",
    "reason": "budgeted use of the mobile data"
  },

  {
    "path": "/mi/telegram-channels/*",
    "action": "BLOCK",
    "reason": "block all channels"
  }
]
```

`GET /`

```json
{
  "devices": {
    "openwrt": {
      "devices": {
        "abcd": "FORCE"
      },
      "dns": [
        "123...",
        "123.123..."
      ]
    },
    "mi": {
      "vpn": {
        "vpn.rethink": "FORCE"
      },
      "dns": ["rethink/blabla"],
      "apps": {
        "*.telegram": "BLOCK",
        "vpn.rethink": "FORCE",
        "beta.telegram": "FORCE"
      },
      "domains": {  
        "wolt.*": "BLOCK",
        "bla.*": "BLOCK"
      },
      "networks": {
        "sim.de": "BLOCK"
      },
      
      "telegram/channels": {
        "*": "BLOCK",
        "bla": "FORCE"
      }
    }
  } 
}
```