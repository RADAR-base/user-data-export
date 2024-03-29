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

package org.radarbase.export.io

import com.opencsv.CSVWriter
import jakarta.ws.rs.core.Context
import org.radarbase.export.Config
import org.radarbase.export.api.User
import org.radarbase.export.exception.ExportTemporarilyFailedException
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Instant
import kotlin.io.path.bufferedWriter

class UserDataWriter(@Context private val config: Config) {
    private val rootPath = Paths.get(config.userDataExportPath!!)

    fun writeUsers(usersToWrite: List<User>) {
        if (usersToWrite.isEmpty()) {
            logger.info("No users required to be written to CSV")
            return
        }

        usersToWrite
            .groupBy { it.createdDate() }
            .forEach { (date, users) -> writeUsers(date, users) }

        logger.info("Written {} user data", usersToWrite.size)
    }

    private fun writeUsers(date: String, usersToWrite: List<User>) {
        val userMaps = usersToWrite.map { it.toMap() }
        val headers = userMaps
            .flatMapTo(mutableSetOf()) { it.keys }
            .toTypedArray()

        logger.debug("Current set of headers are: {}", headers)

        val dateDirectory = Paths.get("$date/${Instant.now()}-${config.userDataExportFile}")
        val path = rootPath.resolve(dateDirectory).normalize()

        logger.debug("Writing user data to {}", path)
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.parent)
            }

            val openOptions = arrayOf(
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
            )
            path.bufferedWriter(options = openOptions).use { fileWriter ->
                CSVWriter(fileWriter).use { csvWriter ->
                    csvWriter.writeNext(headers)
                    userMaps
                        .forEach { user ->
                            csvWriter.writeNext(headers
                                .map { header -> user.getOrDefault(header, "") }
                                .toTypedArray())
                        }
                }
            }
            logger.info("Written {} user data to {}", userMaps.size, path)
        } catch (e: IOException) {
            logger.error("Failed to write user data to {}", path, e)
            throw ExportTemporarilyFailedException("User export failed", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserDataWriter::class.java)
    }
}
