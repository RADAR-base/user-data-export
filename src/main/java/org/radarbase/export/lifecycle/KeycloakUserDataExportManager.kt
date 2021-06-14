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

package org.radarbase.export.lifecycle

import org.glassfish.jersey.server.BackgroundScheduler
import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import org.radarbase.export.Config
import org.radarbase.export.service.KeycloakUserManagementService
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.ext.Provider

@Provider
class KeycloakUserDataExportManager(
        @BackgroundScheduler @Context private val executor: ScheduledExecutorService,
        @Context private val keycloakUserManagementService: KeycloakUserManagementService,
        @Context private val config: Config,
) : ApplicationEventListener {
    private val staleProcessingAge: Duration = Duration.ofMinutes(config.exportIntervalInMin)

    private var exportTask: Future<*>? = null

    override fun onEvent(event: ApplicationEvent?) {
        event ?: return
        when (event.type) {
            ApplicationEvent.Type.INITIALIZATION_APP_FINISHED -> startUserExport()
            ApplicationEvent.Type.DESTROY_FINISHED -> cancelUserExport()
            else -> {}  // do nothing
        }
    }

    @Synchronized
    private fun cancelUserExport() {
        logger.info("Stopping user export service")
        exportTask?.let { task ->
            task.cancel(true)
            exportTask = null
        }
    }

    @Synchronized
    private fun startUserExport() {
        logger.info("Starting user export service")
        if (exportTask != null) {
            return
        }
        logger.info("Running Data Export as a Service with poll interval of {} minutes", staleProcessingAge.toMinutes())

        exportTask = executor.scheduleAtFixedRate(::runUserExport, 0, staleProcessingAge.toSeconds(), TimeUnit.SECONDS)
    }

    private fun runUserExport() {
        try {
            keycloakUserManagementService.exportUserData()
        } catch (ex: Exception) {
            logger.error("Failed to run export user data from keycloak", ex)
        }
    }

    override fun onRequest(requestEvent: RequestEvent?): RequestEventListener? = null

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakUserDataExportManager::class.java)
    }
}
