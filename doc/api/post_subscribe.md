# POST subscribe

## Resource URL

`http://localhost/graphium/api/graphs/{graph}/subscriptions?servername={servername}&url={url}&groupname={groupname}&user={user}&password={password}`

## Parameters

| attribute      | type   | description                            |
| -------------- | ------ | -------------------------------------- |
| **servername** | String | unique name of external central server |
| **graph**      | String | unique graph name                      |
| **groupname**  | String | unique name of subscription group      |
| **url**        | String | URL of external central server         |
| **user**       | String | optional user name for authentication  |
| **password**   | String | optional password for authentication   |
## Example URL

`http://localhost:8081/graphium-satellite-server/api/graphs/osm_at/subscriptions?servername=central_server&url=http://localhost:8080/graphium-server/api&groupname=group_osm_at&user=satellite&password=satellite`

## Example Response

`Subscription for osm_at and server central_server successful`