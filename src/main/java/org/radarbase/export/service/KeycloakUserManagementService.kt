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

import jakarta.ws.rs.core.Context
import org.radarbase.export.Config
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.slf4j.LoggerFactory

class KeycloakUserManagementService(
        @Context private val config: Config,
        @Context private val userDataWriter: UserDataWriter,
        @Context private val keycloakClient: KeycloakClient) {

    fun exportUserData() {
        logger.info("Initializing user-data export from keycloak...")
        val currentNumberOfUsers = keycloakClient.totalNumberOfUsers()
        for (firstUser in 0 until currentNumberOfUsers step config.keycloakUserPageSize) {
            val currentUsers = keycloakClient.readUsers(firstUser, firstUser + config.keycloakUserPageSize)
            val usersToWrite = currentUsers.filterNot { it.isProcessed() }
            logger.info("Found ${usersToWrite.size} unprocessed users from ${currentUsers.size} total number of users")
            if (usersToWrite.isNotEmpty()) {
                userDataWriter.writeUsers(usersToWrite)
                usersToWrite.forEach { keycloakClient.alterUser(it.reset()) }
                logger.info("Exported and overridden ${usersToWrite.size} users")
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakUserManagementService::class.java)
    }
}
