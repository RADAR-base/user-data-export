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
        val currentUsers = keycloakClient.readUsers()
        val usersToWrite = currentUsers.filterNot { it.isProcessed() }.toList()
        logger.info("Found ${usersToWrite.size} unprocessed users from ${currentUsers.size} total number of users")
        if(usersToWrite.isNotEmpty()) {
            userDataWriter.writeUsers(usersToWrite)
            usersToWrite.forEach { keycloakClient.alterUser(it.reset()) }
            logger.info("Exported and overridden ${usersToWrite.size} users")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakDataExportService::class.java)
    }
}
