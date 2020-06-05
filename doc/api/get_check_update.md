# GET check update

## Resource URL

`http://localhost/graphium/api/metadata/graphs/{graph}/checkUpdate?lastImportedVersion={version}`

## Parameters

| attribute               | type   | description                         |
| ----------------------- | ------ | ----------------------------------- |
| **graph**               | String | unique graph name                   |
| **lastImportedVersion** | String | name of last imported graph version |

## Example URL

`http://localhost:8080/graphium-server/api/metadata/graphs/osm_at/checkupdate?lastImportedVersion=200603`

## Example Response

```json
{"update":"false"}
```

