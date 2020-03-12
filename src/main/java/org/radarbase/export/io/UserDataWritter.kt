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
import org.radarbase.export.Config
import org.radarbase.export.api.User
import org.radarbase.export.exception.ExportTemporarilyFailedException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class UserDataWriter(private val config: Config) {

    private val rootPath = Paths.get(config.userDataExportPath!!)

    fun writeUsers(usersToWrite: List<User>) {
        if(usersToWrite.isEmpty()) {
            logger.info("No users required to be written to CSV")
            return
        }

        try {
            val dateDirectory = Paths.get("${directoryDateFormatter.format(Instant.now())}/${directoryTimeFormatter.format(Instant.now())}/${config.userDataExportFile}")
            val fullPath = rootPath.resolve(dateDirectory).normalize()
            logger.info("Writing user data to $fullPath")
            val file = prepareFile(fullPath) ?: throw IOException("Could not create file")
            val csvWriter = CSVWriter(file.bufferedWriter())
            val headers = usersToWrite.flatMap { it.toMap().keys }.toSet()
            logger.info("Current set of headers are : $headers")
            val output = mutableListOf(headers.toTypedArray())
            // remap the values for final set of headers
            output.addAll(usersToWrite.map {
                user -> headers.map { header -> user.toMap().getOrDefault(header, "") }.toTypedArray()
            }.toList())

            csvWriter.use {
                it.writeAll(output)
            }
            logger.info("Written ${usersToWrite.size} user data to $fullPath")
        } catch (e: IOException) {
            logger.error("Failed to write user data", e)
            throw ExportTemporarilyFailedException("User export failed", e)
        }
    }

    private fun prepareFile(fullPath: Path): File? {
        if (Files.notExists(fullPath)) {
            File(fullPath.toUri()).parentFile.mkdirs()
            return Files.createFile(fullPath).toFile()
        } else {
            return File(fullPath.toUri())
        }
    }

    companion object {
        private val directoryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.of("UTC"))
        private val directoryTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withZone(ZoneId.of("UTC"))
        private val logger = LoggerFactory.getLogger(UserDataWriter::class.java)

    }
}
