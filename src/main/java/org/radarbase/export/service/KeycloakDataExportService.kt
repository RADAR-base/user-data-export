package org.radarbase.export.service

import org.radarbase.export.Config
import org.radarbase.export.api.User
import org.radarbase.export.api.User.Companion.IS_PROCESSED
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.slf4j.LoggerFactory

class KeycloakDataExportService (config: Config) {

    private val userDataWriter = UserDataWriter(config)
    private val keycloakClient = KeycloakClient(config)

    fun exportUserData() {
        logger.info("Initializing user-data export from keycloak...")
        val currentUsers = keycloakClient.readUsers()
                logger.info("current users $currentUsers")

        val usersToWrite = currentUsers
                .filterNot { it.isProcessed() }.toList()
        logger.info("users to process $usersToWrite")
        val usersToOverride = userDataWriter.writeUsers(usersToWrite)
        usersToOverride.forEach { keycloakClient.alterUser(it.reset()) }
        logger.info("Exported and overridden ${usersToOverride.size} users")
    }

    private fun User.isProcessed(): Boolean {
            logger.info("Attributes $attributes")
            logger.info("isProcessed ${(attributes?.getValue(IS_PROCESSED)?.first()?.toBoolean() ?: false)}")
            return (attributes?.getValue(IS_PROCESSED)?.first()?.toBoolean() ?: false)
    }
    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakDataExportService::class.java)
    }
}
