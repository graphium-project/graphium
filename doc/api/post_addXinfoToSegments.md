# POST add XInfo to segments

## Resource URL

`http://localhost/graphium/api/segments/graphs/{graph}/versions/{version}/xinfos`

## Parameters

| attribute   | type           | description                                                  |
| ----------- | -------------- | ------------------------------------------------------------ |
| **graph**   | String         | unique graph name                                            |
| **version** | String         | unique graph version or **current** for currently active graph version |
| **file**    | Multipart File | XInfos as JSON file                                          |

## Example URL

`curl -X POST "http://localhost:8080/graphium/api/segments/graphs/osm_at/versions/200603/xinfos" -F "file=@D:/graphium/json/osm_at_segments_xinfo.json"`

## Response

No Response.