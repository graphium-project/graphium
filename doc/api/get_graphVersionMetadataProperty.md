# GET graph version metadata's property

## Resource URL

`http://localhost/graphium/api/metadata/graphs/{graph}/versions/{version}/{propertyName}`

## Parameters

| **Attribut**     | **Datentyp** | **Beschreibung**                         |
| ---------------- | ------------ | ---------------------------------------- |
| **graph**        | String       | unique graph name                        |
| **version**      | String       | unique graph version or **current** for currently active graph version |
| **propertyName** | String       | metadata's property name                 |

## Example URL

`http://localhost:8080/graphium-central-server/api/metadata/graphs/osm_at/versions/170828/state`

## Example Response

```json
{"state":"INITIAL"}
```

