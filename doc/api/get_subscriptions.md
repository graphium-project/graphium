# GET subscriptions for all graphs

## Resource URL

`http://localhost/graphium/api/subscriptions`

## Parameters
No parameters.

## Example URL

`http://localhost:8080/graphium-central-server/api/subscriptions`

## Example Response

```json
[{
  "serverName":"satellite_graphserver_neo4j",
  "viewName":"gip_sbg",
  "url":"http://localhost:7474/graphium",
  "timestamp":1496151397885,
  "subscriptionGroupName":"group_gip_sbg"
},{
  "serverName":"MOWI89_satellite_server1",
  "viewName":"gip_at_frc_0",
  "url":"http://localhost:8081/graphium-satellite-server/api",
  "timestamp":1496133143306,
  "subscriptionGroupName":"group_gip_at_frc_0"
},{
  "serverName":"satellite_graphserver_neo4j",
  "viewName":"gip_at_frc_0",
  "url":"http://localhost:7474/graphium",
  "timestamp":1496146001361,
  "subscriptionGroupName":"group_gip_at_frc_0"
},{
  "serverName":"satellite_graphserver_neo4j",
  "viewName":"gip_at_frc_0_4_test",
  "url":"http://localhost:7474/graphium",
  "timestamp":1496218364341,
  "subscriptionGroupName":"group_gip_at_frc_0_4_test_neo"
},{
  "serverName":"MOWI89_satellite_server1",
  "viewName":"gip_at_frc_0_4_test",
  "url":"http://localhost:8081/graphium-satellite-server/api",
  "timestamp":1496218340033,
  "subscriptionGroupName":"group_gip_at_frc_0_4_test"
},{
  "serverName":"satellite_server",
  "viewName":"osm_at",
  "url":"http://localhost:8081/graphium-satellite-server/api",
  "timestamp":1503991646586,
  "subscriptionGroupName":"group_osm_at"
}]
```
# GET subscriptions for one specific graph

## Resource URL

`http://localhost/graphium/api/graphs/{graph}/subscriptions`

## Parameters

| attribute | type   | description                  |
| --------- | ------ | ---------------------------- |
| **graph** | String | unique graph name (optional) |

## Example URL

`http://localhost:8080/graphium/api/graphs/osm_at/subscriptions`

## Example Response

```json
[{
  "serverName":"satellite_server",
  "viewName":"osm_at",
  "url":"http://localhost:8081/graphium-satellite-server/api",
  "timestamp":1503991646586,
  "subscriptionGroupName":"group_osm_at"
}]
```