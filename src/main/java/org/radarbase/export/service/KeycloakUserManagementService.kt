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
import org.radarbase.export.api.User
import org.radarbase.export.api.User.Companion.PROJECT_NAME
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.radarbase.jersey.exception.HttpNotFoundException
import org.slf4j.LoggerFactory
import javax.ws.rs.core.Context

class KeycloakUserManagementService(
        @Context private val config: Config,
        @Context private val userDataWriter: UserDataWriter,
        @Context private val keycloakClient: KeycloakClient) {
    private var lastReadUserCount = 0
    fun exportUserData() {
        logger.info("Initializing user-data export from keycloak...")
        val currentNumberOfUsers = keycloakClient.totalNumberOfUsers()
        while (currentNumberOfUsers > lastReadUserCount) {
            val currentUsers = keycloakClient.readUsers(lastReadUserCount, lastReadUserCount + config.keycloakUserPageSize)
            val usersToWrite = currentUsers.filterNot { it.isProcessed() }.toList()
            logger.info("Found ${usersToWrite.size} unprocessed users from ${currentUsers.size} total number of users")
            if (usersToWrite.isNotEmpty()) {
                userDataWriter.writeUsers(usersToWrite)
                usersToWrite.forEach { keycloakClient.alterUser(it.reset()) }
                logger.info("Exported and overridden ${usersToWrite.size} users")
            }
            lastReadUserCount += currentUsers.size
        }
    }


    fun setProjectName(userId: String, projectName: String) : User {
        val user = keycloakClient.readUser(userId) ?: throw HttpNotFoundException("user_not_found", "Could not read user $userId from keycloak")
        val attributes = user.attributes?.toMutableMap() ?: mutableMapOf()
        val new = user.copy(
                attributes = attributes.plus(PROJECT_NAME to listOf(projectName))
        )
        logger.info("User to update : ", new )
        keycloakClient.alterUser(new)
        return keycloakClient.readUser(userId) ?: throw HttpNotFoundException("user_not_found", "Could not read user $userId from keycloak")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakUserManagementService::class.java)
    }
}
