package org.radarbase.export

import org.radarbase.export.service.KeycloakDataExportService
import org.radarbase.jersey.config.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val logger: Logger = LoggerFactory.getLogger("org.radarbase.export.Main")

fun main(args: Array<String>) {
    val config: Config = ConfigLoader.loadConfig("data-export-config.yml", args)

    logger.info("Starting user-data-export service")
    Application(config).start()
}

class Application (private val config: Config) {

    fun start() {
        logger.info("Running as a Service with poll interval of {} seconds", config.exportIntervalInSeconds)
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
