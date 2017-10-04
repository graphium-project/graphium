# POST import graph version

## Resource URL

`http://localhost/graphium/api/segments/graphs/{graph}/versions/{version}?overrideIfExists={overrideIfExists}`

## Parameters

| **Attribut**         | **Datentyp**   | **Beschreibung**                         |
| -------------------- | -------------- | ---------------------------------------- |
| **graph**            | String         | unique graph name                        |
| **version**          | String         | unique graph version or **current** for currently active graph version |
| **overrideIfExists** | boolean        | optional; if true an existing graph version will be overwritten; default **true** |
| **file**             | Multipart File | graph version as JSON file               |

## Example URL

`curl -X POST "http://localhost:8080/graphium-central-server/api/segments/graphs/osm_at/versions/170828?overrideIfExists=true" -F "file=@D:/graphium/json/osm_at.json"`

## Example Response

```json
{
  "id" : 56,
  "graphName" : "osm_at",
  "version" : "170828",
  "originGraphName" : "osm_at",
  "originVersion" : "161201",
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
}
```