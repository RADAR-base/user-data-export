package org.radarbase.export

import org.radarbase.export.service.KeycloakDataExportService
import org.radarbase.jersey.config.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val logger: Logger = LoggerFactory.getLogger("org.radarbase.export.Main")

fun main(args: Array<String>) {
    val config: Config = ConfigLoader.loadConfig("data-export-config.yml", args)

    logger.info("Starting user-data-export service")
    Application(config).start();
}

class Application (private val config: Config) {

    fun start() {
        KeycloakDataExportService(config)
            .exportUserData()

    }

}
