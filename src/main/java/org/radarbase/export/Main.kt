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

import org.radarbase.jersey.GrizzlyServer
import org.radarbase.jersey.config.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val logger: Logger = LoggerFactory.getLogger("org.radarbase.export.Main")

fun main(args: Array<String>) {
    val config: Config = ConfigLoader.loadConfig("data-export-config.yml", args)

    val resources = ConfigLoader.loadResources(config.resourceConfig, config)
    logger.info("Starting keycloak user-management service")
    val server = GrizzlyServer(config.baseUri, resources)
    server.listen()

}

