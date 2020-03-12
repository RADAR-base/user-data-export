# keycloak user data exporter

A simple Kotlin application to export user information from Keycloak and store it on a CSV file then remove personally identifiable information.

All processed users will be markes as processed in Keycloak and will not be processed in subsequent reads. 


## Docker-compose Usage 
```bash
docker run -it radarbase/user-data-exporter:latest "user-data-exporter" "path/to/data-export-config.yml"
```
## Configuration
Sample configuration file with all configurable parameters
```yaml

keycloakUrl: "http://localhost:8080/auth/"
clientId: "user-data-export" # default is user-data-export
clientSecret: "client secret of the above clientId"
realmName: "keycloak realm name"
userDataExportFile: "keycloak-user-export.csv" # default is keycloak-user-export.csv
userDataExportPath: "etc/" # default is etc/
exportIntervalInSeconds: 120 # default is 120 seconds
```

## Creating client in keycloak
This implementation works with having a separate client for confidential communication (client credentials grant type).
This configuration should only done for trusted applications and deployments.

- Create a client in keycloak and set access type **confidential** and **enable service account**
- Note down the client-id and client-secret from Credentials tab
- Add manage-user scope in Scopes
- Add manage-user scope in Service-Account-Roles


Tested with Keycloak:9.0.0 image. 
