package org.radarbase.export

data class Config(
        var keycloakUrl: String = "http://localhost:8080/auth/",
        var clientId: String = "user-data-export",
        var clientSecret: String,
        var realmName: String?,
        var userDataExportFile: String? = "keycloak-user-export.csv",
        var userDataExportPath: String? = "etc/",
        var exportIntervalInSeconds: Long = 120
   )
