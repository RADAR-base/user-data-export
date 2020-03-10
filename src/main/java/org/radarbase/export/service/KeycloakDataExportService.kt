package org.radarbase.export.service

import org.radarbase.export.Config
import org.radarbase.export.keycloak.KeycloakClient
import org.slf4j.LoggerFactory

class KeycloakDataExportService (private val config: Config) {

    fun exportUserData() {
        logger.info("Sending request to export user data")
        val users = KeycloakClient(config).also { it.readUsers() }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakDataExportService::class.java)
    }
}
