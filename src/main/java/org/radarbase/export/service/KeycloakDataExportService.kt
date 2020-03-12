/*
 *
 *  Copyright  2020  The Hyve
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */

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
