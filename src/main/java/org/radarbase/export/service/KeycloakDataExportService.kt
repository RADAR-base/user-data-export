package org.radarbase.export.service

import org.radarbase.export.Config
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.slf4j.LoggerFactory

class KeycloakDataExportService (config: Config) {

    private val userDataWriter = UserDataWriter(config)
    private val keycloakClient = KeycloakClient(config)

    fun exportUserData() {
        logger.info("Initializing user-data export from keycloak...")
        val users = keycloakClient.readUsers()
        userDataWriter.writeUsers(usersToWrite = users)
        users.forEach { keycloakClient.alterUser(it) }
        logger.info("Exported and overridden ${users.size} users")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakDataExportService::class.java)
    }
}
