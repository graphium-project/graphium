# GET list graph versions

## Resource URL

List all versions:

`http://localhost/graphium/api/metadata/graphs/{graph}/versions`

List versions within a time period:

`http://localhost/graphium/api/metadata/graphs/{graph}/versions?startTimestamp={startTimestamp}&endTimestamp={endTimestamp}`

## Parameters

| attribute          | type   | description                              |
| ------------------ | ------ | ---------------------------------------- |
| **graph**          | String | unique graph name                        |
| **startTimestamp** | long   | optional; start of requested time period |
| **endTimestamp**   | long   | optional; end of requested time period   |


## Example URL

`http://localhost:8080/graphium-server/api/metadata/graphs/osm_at/versions`

## Example Response

```json
[{
  "id" : 56,
  "graphName" : "osm_at",
  "version" : "200603",
  "originGraphName" : "osm_at",
  "originVersion" : "200603",
  "state" : "INITIAL",
  "validFrom" : 1485262403320,
  "coveredArea" : "POLYGON ((9.528048700000001 46.4084712, 9.528048700000001 49.0140693, 17.156510700000002 49.0140693, 17.156510700000002 46.4084712, 9.528048700000001 46.4084712))",
  "segmentsCount" : 144366,
  "connectionsCount" : 296001,
  "accessTypes" : [ ],
  "tags" : { },
  "source" : "OSM",
  "type" : "waysegment",
  "creationTimestamp" : 0,
  "storageTimestamp" : 1503908692892,
  "creator" : "Importer"
}]
```
