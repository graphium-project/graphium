# GET check update

## Resource URL

`http://localhost/graphium/api/metadata/graphs/{graph}/checkUpdate?lastImportedVersion={version}`

## Parameters

| **Attribut**            | **Datentyp** | **Beschreibung**                    |
| ----------------------- | ------------ | ----------------------------------- |
| **graph**               | String       | unique graph name                   |
| **lastImportedVersion** | String       | name of last imported graph version |

## Example URL

`http://localhost:8080/graphium-central-server/api/metadata/graphs/osm_at/checkupdate?lastImportedVersion=170828`

## Example Response

```json
{"update":"false"}
```

