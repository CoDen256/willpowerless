Law {

 -> /dev/mi/apps/telegram.beta : FORCE
 -> /dev/mi/apps/* : BLOCK
 -> /dev/mi/apps/telegram.beta/channels/* : BLOCK
}


`GET /`

```json

{
  "dev": {
    "mi": {
      "apps": {
        "telegram.beta": {
          "ruling": "FORCE",
          "channels": {
            "*": {
              "ruling": "BLOCK"
            }
          }
        },
        "*": {
          "ruling": "BLOCK"
        }
      }
    }
  }
}

```

`GET /dev/mi/apps/`

```json
{

  "telegram.beta": {
    "ruling": "FORCE",
    "channels": {
      "*": {
        "ruling": "BLOCK"
      }
    }
  },
  "*": {
    "ruling": "BLOCK"
  }
}
```


`GET /dev/mi/apps/`

```json
{

  "telegram.beta": {
    "ruling": "FORCE",
    "channels": {
      "*": {
        "ruling": "BLOCK"
      }
    }
  },
  "*": {
    "ruling": "BLOCK"
  }
}
```

`GET /dev/mi/apps/telegram.beta/ruling`

```json
{
  "ruling": "FORCE"
}
```

`GET /dev/mi/apps/something`

```json
{
  "ruling": "BLOCK"
}
```