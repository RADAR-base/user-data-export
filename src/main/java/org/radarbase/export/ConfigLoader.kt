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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object ConfigLoader {

    /**
     * Load a configuration from YAML file. The filename is searched in the current working
     * directory. This exits with a usage information message if the file cannot be loaded.
     *
     * @throws IllegalArgumentException if a file matching configFileName cannot be found
     */
    inline fun <reified T> loadConfig(fileName: String, args: Array<String>, mapper: ObjectMapper? = null): T =
            loadConfig(fileName, args, T::class.java, mapper)

    @JvmOverloads
    fun <T> loadConfig(fileName: String, args: Array<String>, clazz: Class<T>, mapper: ObjectMapper? = null): T {
        val configFileName = when {
            args.size == 1 -> args[0]
            Files.exists(Paths.get(fileName)) -> fileName
            else -> null
        }
        requireNotNull(configFileName) { "Configuration not provided." }

        val configFile = File(configFileName)
        logger.info("Reading configuration from ${configFile.absolutePath}")
        try {
            val localMapper = mapper ?: ObjectMapper(YAMLFactory())
                    .registerModule(KotlinModule())
            return localMapper.readValue(configFile, clazz)
        } catch (ex: IOException) {
            logger.error("Usage: <command> [$fileName]")
            logger.error("Failed to read config file $configFile: ${ex.message}")
            exitProcess(1)
        }
    }

    val logger: Logger = LoggerFactory.getLogger(ConfigLoader::class.java)
}
