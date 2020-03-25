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

package org.radarbase.export.inject

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.radarbase.export.Config
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.radarbase.export.lifecycle.KeycloakUserDataExportManager
import org.radarbase.export.service.KeycloakUserManagementService
import org.radarbase.jersey.auth.AuthValidator
import org.radarbase.jersey.auth.jwt.EcdsaJwtTokenValidator
import org.radarbase.jersey.config.ConfigLoader
import org.radarbase.jersey.config.EnhancerFactory
import org.radarbase.jersey.config.JerseyResourceEnhancer
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class UserManagementEnhancerFactory(private val config: Config) : EnhancerFactory {
    override fun createEnhancers(): List<JerseyResourceEnhancer> = listOf(
            UserManagementEnhancer(config),
            ConfigLoader.Enhancers.generalException,
            ConfigLoader.Enhancers.httpException)

    class UserManagementEnhancer(private val config: Config): JerseyResourceEnhancer {


        override val classes: Array<Class<*>>  get() {
            return if (config.enableCors == true) {
                arrayOf(
                        ConfigLoader.Filters.logResponse,
                        ConfigLoader.Filters.cors)
            } else {
                arrayOf(
                        ConfigLoader.Filters.logResponse
                )
            }
        }

        override val packages: Array<String> = arrayOf(
                "org.radarbase.export.resource",
                "org.radarbase.export.lifecycle")

        override fun enhanceBinder(binder: AbstractBinder) {
            binder.apply {
                bind(config)
                        .to(Config::class.java)

                bind(client)
                        .to(OkHttpClient::class.java)

                bind(OBJECT_MAPPER)
                        .to(ObjectMapper::class.java)

                bind(KeycloakClient::class.java)
                        .to(KeycloakClient::class.java)
                        .`in`(Singleton::class.java)

                bind(UserDataWriter::class.java)
                        .to(UserDataWriter::class.java)
                        .`in`(Singleton::class.java)

                bind(KeycloakUserManagementService::class.java)
                        .to(KeycloakUserManagementService::class.java)
                        .`in`(Singleton::class.java)

                binder.bind(EcdsaJwtTokenValidator::class.java)
                        .to(AuthValidator::class.java)
                        .`in`(Singleton::class.java)
            }
        }
    }

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
                .registerModule(JavaTimeModule())
                .registerModule(KotlinModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        private val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }
}
