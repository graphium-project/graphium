# DELETE unsubscribe

## Resource URL

`http://localhost/graphium/api/graphs/{graph}/subscriptions?servername={servername}`

## Parameters

| **Attribut**   | **Datentyp** | **Beschreibung**                       |
| -------------- | ------------ | -------------------------------------- |
| **servername** | String       | unique name of external central server |
| **graph**      | String       | unique graph name                      |
## Example URL

`http://localhost:8081/graphium-satellite-server/api/graphs/osm_at/subscriptions?servername=central_server`

## Example Response

`unsubscription successful`