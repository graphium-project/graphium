# DELETE XInfo from connections

## Resource URL

`http://localhost/graphium/api/connections/graphs/{graph}/versions/{version}/xinfos`

## Parameters

| attribute   | type   | description                                                  |
| ----------- | ------ | ------------------------------------------------------------ |
| **graph**   | String | unique graph name                                            |
| **version** | String | unique graph version or **current** for currently active graph version |
| **type**    | String | XInfo's type                                                 |

## Example URL

`http://localhost:8080/graphium-server/api/connections/graphs/osm_at/versions/200603/xinfos/turnprobs`

## ``Response

"Deleted"