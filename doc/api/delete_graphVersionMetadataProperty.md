# DELETE graph version metadata's property

## Resource URL

`http://localhost/graphium/api/metadata/graphs/{graph}/versions/{version}/{propertyName}`

## Parameters

| attribute         | type   | description                                                  |
| ----------------- | ------ | ------------------------------------------------------------ |
| **graph**         | String | unique graph name                                            |
| **version**       | String | unique graph version or **current** for currently active graph version |
| **propertyName**  | String | metadata's property name                                     |
| **value**         | Object | property's value                                             |
| **groupname**     | String | optional; only needed in case of changing state to **ACTIVATE**; name of group to activate a graph version for; if not set activation will be done for all subscribers |
| **segmentscount** | int    | optional; only needed in case of changing state to **ACTIVATE**; number of segments in graph version to activate; needed for validation issues |

## Example URL

`http://localhost:8080/graphium/api/metadata/graphs/osm_at/versions/200603/description`

## Example Response

```json
{"description":null}
```