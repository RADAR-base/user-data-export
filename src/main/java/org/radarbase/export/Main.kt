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

package org.radarbase.export

import org.radarbase.export.service.KeycloakDataExportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val logger: Logger = LoggerFactory.getLogger("org.radarbase.export.Main")

fun main(args: Array<String>) {
    val config: Config = ConfigLoader.loadConfig("data-export-config.yml", args)
    Application(config).start()
}

class Application (private val config: Config) {

    fun start() {
        logger.info("Running Data Export as a Service with poll interval of {} seconds", config.exportIntervalInSeconds)
        logger.info("Press Ctrl+C to exit...")
        val executorService = Executors.newSingleThreadScheduledExecutor()

        executorService.scheduleAtFixedRate(::runDataExport,
                config.exportIntervalInSeconds / 4, config.exportIntervalInSeconds, TimeUnit.SECONDS)

        try {
            Thread.sleep(java.lang.Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            logger.info("Interrupted, shutting down...")
            executorService.shutdownNow()
        }
    }

    private fun runDataExport() = KeycloakDataExportService(config).exportUserData()

}
