package org.radarbase.export.io

import com.opencsv.CSVWriter
import org.radarbase.export.Config
import org.radarbase.export.api.User
import org.radarbase.export.exception.ExportTemporarilyFailedException
import org.radarbase.export.logger
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
        try {
            val dateDirectory = Paths.get("${directoryDateFormatter.format(Instant.now())}/${config.userDataExportFile}")
            logger.info("parent directory $dateDirectory")
            val fullPath = rootPath.resolve(dateDirectory).normalize()
            logger.info("Writing user data to $fullPath")
            val file = prepareFile(fullPath) ?: throw IOException("Could not create file")
            val csvWriter = CSVWriter(file.bufferedWriter())

            val headers = usersToWrite.flatMap { it.toMap().keys }.toSet()
            logger.info("Current set of headers are : $headers")

            val output = mutableListOf(headers.toTypedArray())
            output.addAll(usersToWrite.map { user ->
                // remapping the values for final set of headerss
                headers.map { header -> user.toMap().getOrDefault(header, "") }.toTypedArray()
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
        }
        return File(fullPath.toUri())
    }

    companion object {
        private val directoryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.of("UTC"))
    }
}
