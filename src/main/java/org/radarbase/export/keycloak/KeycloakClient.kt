package org.radarbase.export.keycloak

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.radarbase.export.Config
import org.radarbase.export.api.User
import org.radarbase.export.exception.BadGatewayException
import org.radarbase.export.exception.ForbiddenException
import org.radarbase.export.exception.NotAuthorizedException
import org.slf4j.LoggerFactory
import java.net.MalformedURLException
import java.time.Duration
import java.time.Instant

class KeycloakClient (private val config: Config) {

    private val keycloakBaseUrl: HttpUrl = config.keycloakUrl.toHttpUrlOrNull()
        ?: throw MalformedURLException("Cannot parse base URL ${config.keycloakUrl} as an URL")
    private val clientId: String = config.clientId
    private val clientSecret: String = config.clientSecret
    private val realmName: String? = config.realmName
    private val httpClient = OkHttpClient()
    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val userListReader = mapper.readerFor(object : TypeReference<List<User>>(){})


    private var token: String? = null
    private var expiration: Instant? = null

    private val validToken: String?
        get() {
            val localToken = token ?: return null
            expiration?.takeIf { it > Instant.now() } ?: return null
            return localToken
        }

    private fun ensureToken(): String {
        var localToken = validToken

        return if (localToken != null) {
            localToken
        } else {
            val url = keycloakBaseUrl.resolve("realms/$realmName/protocol/openid-connect/token")!!
            logger.info("Url is $url")
            val request = Request.Builder().apply {
                url(url)
                post(FormBody.Builder().apply {
                    add("grant_type", "client_credentials")
                    add("client_id", clientId)
                    add("client_secret", clientSecret)
                }.build())
                header("Content-Type", "application/x-www-form-urlencoded")
                header("Authorization", Credentials.basic(clientId, clientSecret))
            }.build()


            val result = mapper.readTree(execute(request))
            localToken = result["access_token"].asText()
                ?: throw BadGatewayException("ManagementPortal did not provide an access token")
            expiration = Instant.now() + Duration.ofSeconds(result["expires_in"].asLong()) - Duration.ofMinutes(5)
            token = localToken
            localToken
        }
    }

    private fun execute(request: Request): String {
        return httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.string()
                    ?: throw BadGatewayException("keycloak did not provide a result")
            } else {
                logger.error("Cannot connect to keycloak {} {}", response.code, response.body)
                when (response.code)
                {
                    403 -> throw ForbiddenException("Current credentials are not allowed to perform this action ${request.method} on ${request.url}")
                    401 -> throw NotAuthorizedException("Current credentials are not authorized to perform this action ${request.method} on ${request.url}")
                }

                throw BadGatewayException("Cannot connect to keycloak : Response-code ${response.code} ${response.body}")
            }
        }
    }

    fun readUsers(): List<User> {
        logger.debug("Requesting for users from realm $realmName")
        val url = keycloakBaseUrl.resolve("admin/realms/$realmName/users")!!
        logger.debug("Users Url is $url")
        val request = Request.Builder().apply {
            url(url)
            header("Authorization", "Bearer ${ensureToken()}")
        }.build()

        return userListReader.readValue<List<User>>(execute(request))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakClient::class.java)
    }
}
