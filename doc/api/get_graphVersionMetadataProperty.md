# GET graph version metadata's property

## Resource URL

`http://localhost/graphium/api/metadata/graphs/{graph}/versions/{version}/{propertyName}`

## Parameters

| attribute        | type   | description                                                  |
| ---------------- | ------ | ------------------------------------------------------------ |
| **graph**        | String | unique graph name                                            |
| **version**      | String | unique graph version or **current** for currently active graph version |
| **propertyName** | String | metadata's property name                                     |

## Example URL

`http://localhost:8080/graphium/api/metadata/graphs/osm_at/versions/200603/state`

## Example Response

```json
{"state":"INITIAL"}
```

