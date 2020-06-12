# GET status

## Resource URL

`http://localhost/graphium/api/status`

## Parameters

No parameters.

## Example URL

`http://localhost:8080/graphium/api/status`

## Example Response

```json
{
  "serverName":"central_graphserver",
  "graphStatuses":[{
    "graph":"gip_at_frc_0_4",
	"originalGraph":"gip_at_frc_0_4",
	"versionLastImported":"17_02_170627_4",
	"versionCurrentlyActive":"17_02_170531"
  },{
    "graph":"gip_at",
	"originalGraph":"gip_at",
	"versionLastImported":"test",
	"versionCurrentlyActive":"test"
  },{
    "graph":"gip_at_frc_0_4_test",
	"originalGraph":"gip_at_frc_0_4",
	"versionLastImported":"17_02_170531_5",
	"versionCurrentlyActive":"17_02_170531_5"
  },{
    "graph":"osm_at",
	"originalGraph":"osm_at",
	"versionLastImported":"170828",
	"versionCurrentlyActive":null
  }],
  "runningImports":0
}
```
