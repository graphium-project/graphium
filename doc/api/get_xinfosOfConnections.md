# GET XInfos of connections

## Resource URL

`http://localhost/graphium/api/connections/graphs/{graph}/versions/{version}/xinfos/{type}`

## Parameters

| attribute   | type   | description                                                  |
| ----------- | ------ | ------------------------------------------------------------ |
| **graph**   | String | unique graph name                                            |
| **version** | String | unique graph version or **current** for currently active graph version |
| **type**    | String | XInfo's type                                                 |


## Example URL

`http://localhost:8080/graphium/api/connections/graphs/osm_at/versions/200603/xinfos/turnprob`

## Example Response

```json
{
  "basesegment" : [ {
	"id" : 932303,
	"connection" : [ {
	  "nodeId" : 460005347,
	  "toSegmentId" : 461001351,
	  "xInfo" : {
		"turnprob" : [ {
		  "nodeChangeFactor" : 1.0019000704053027,
		  "probability" : 0.7483709273182957,
		  "turnIntoProbability" : 1.0,
		  "probabilityIndefinite" : false,
		  "turnIntoProbabilityIndefinite" : false,
		  "changeFactorIndefinite" : false
		} ]
	  }
	}, {
	  "nodeId" : 890002286,
	  "toSegmentId" : 901405426,
	  "xInfo" : {
		"turnprob" : [ {
		  "nodeChangeFactor" : 1.0,
		  "probability" : 1.0,
		  "turnIntoProbability" : 1.0,
		  "probabilityIndefinite" : false,
		  "turnIntoProbabilityIndefinite" : false,
		  "changeFactorIndefinite" : false
		} ]
	  }
	} ]
}
```