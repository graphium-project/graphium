# GET XInfos of segments

## Resource URL

`http://localhost/graphium/api/segments/graphs/{graph}/versions/{version}/xinfos/{type}`

## Parameters

| attribute   | type   | description                                                  |
| ----------- | ------ | ------------------------------------------------------------ |
| **graph**   | String | unique graph name                                            |
| **version** | String | unique graph version or **current** for currently active graph version |
| **type**    | String | XInfo's type                                                 |


## Example URL

`http://localhost:8080/graphium/api/segments/graphs/osm_at/versions/200603/xinfos/default`

## Example Response

```json
{
  "basesegment" : [ {
	"id" : 23003404,
	"xInfo" : {
	  "default" : [ {
		"directionTow" : false,
		"buslane" : "bkw"
	  } ]
	},
	"connection" : [ ]
  }, {
	"id" : 23009511,
	"xInfo" : {
	  "default" : [ {
		"directionTow" : false,
		"buslane" : "bkw"
	  } ]
	},
	"connection" : [ ]
  } ]
}
```