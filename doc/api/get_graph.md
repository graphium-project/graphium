# GET graph

## Resource URL

`http://localhost/graphium/api/segments/graphs/{graph}/versions/{version}?ids={ids}&compress={compress}`

## Parameters

| attribute    | type       | description                                                  |
| ------------ | ---------- | ------------------------------------------------------------ |
| **graph**    | String     | unique graph name                                            |
| **version**  | String     | unique graph version or **current** for currently active graph version |
| **ids**      | List<Long> | comma separated list of segment IDs                          |
| **compress** | Boolean    | if true the result will be compressed (zip)                  |

## Example URL

`http://localhost:8080/graphium-server/api/segments/graphs/osm_at/versions/200603?ids=149673`

## Example Response

```json
{
  "graphVersionMetadata" : {
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
  },
  "waysegment" : [ {
    "id" : 149673,
    "connection" : [ {
          "nodeId" : 377950,
          "toSegmentId" : 33170588,
          "access" : [ "PRIVATE_CAR" ]
    }, {
          "nodeId" : 377950,
          "toSegmentId" : 4423541,
          "access" : [ "PRIVATE_CAR" ]
    }, {
          "nodeId" : 377950,
          "toSegmentId" : 9223338866257775800,
          "access" : [ "PRIVATE_CAR" ]
    }, {
          "nodeId" : 378439,
          "toSegmentId" : 51393220,
          "access" : [ "PRIVATE_CAR" ]
    }, {
          "nodeId" : 378439,
          "toSegmentId" : 9703830,
          "access" : [ "PRIVATE_CAR" ]
    } ],
    "geometry" : "LINESTRING (16.280692600000002 48.1975883, 16.2807187 48.1984513, 16.2807192 48.198647, 16.280547300000002 48.1989838, 16.2804542 48.1991473,16.2799596 48.199552800000006, 16.279789348.199760600000005, 16.2796171 48.199997800000006, 16.2795612 48.200185100000006, 16.2795528 48.200204500000005, 16.2793199 48.200814, 16.2791128 48.2013533, 16.279066800000003 48.2014738, 16.2790629 48.2014834, 16.2790134 48.201599300000005, 16.278952200000003 48.202226700000004, 16.278953 48.202390300000005, 16.2788907 48.203642300000006, 16.2788799 48.2038742, 16.2788752 48.2039764)",
    "name" : "Waidhausenstraße",
    "wayId" : 149673,
    "startNodeIndex" : 0,
    "startNodeId" : 377950,
    "endNodeIndex" : 19,
    "endNodeId" : 378439,
    "maxSpeedTow" : 30,
    "maxSpeedBkw" : 30,
    "calcSpeedTow" : 30,
    "calcSpeedBkw" : 30,
    "lanesTow" : 1,
    "lanesBkw" : 1,
    "frc" : 4,
    "formOfWay" : "PART_OF_SINGLE_CARRIAGEWAY",
    "accessTow" : [ "PRIVATE_CAR" ],
    "accessBkw" : [ "PRIVATE_CAR" ],
    "tunnel" : false,
    "bridge" : false,
    "urban" : false
  } ]
}
```
