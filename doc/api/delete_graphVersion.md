# DELETE graph version

## Resource URL

`http://localhost/graphium/api/segments/graphs/{graph}/versions/{version}?keepMetadata={keepMetadata}`

## Parameters

| attribute        | type    | description                                                  |
| ---------------- | ------- | ------------------------------------------------------------ |
| **graph**        | String  | unique graph name                                            |
| **version**      | String  | unique graph version or **current** for currently active graph version |
| **keepMetadata** | boolean | optional; if true metadata will not be deleted, but state will be set to **DELETED**; default **true** |

## Example URL

`http://localhost:8080/graphium/api/segments/graphs/osm_at/versions/200603?keepMetadata=true`

## Example Response

```json
{
  "id" : 56,
  "graphName" : "osm_at",
  "version" : "200603",
  "originGraphName" : "osm_at",
  "originVersion" : "200603",
  "state" : "DELETED",
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
}
```