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

import org.glassfish.jersey.internal.inject.AbstractBinder
import org.radarbase.export.Config
import org.radarbase.export.io.UserDataWriter
import org.radarbase.export.keycloak.KeycloakClient
import org.radarbase.export.service.KeycloakUserManagementService
import org.radarbase.jersey.auth.AuthConfig
import org.radarbase.jersey.enhancer.EnhancerFactory
import org.radarbase.jersey.enhancer.Enhancers
import org.radarbase.jersey.enhancer.JerseyResourceEnhancer
import org.radarbase.jersey.filter.Filters
import org.radarbase.jersey.service.ProjectService
import jakarta.inject.Singleton

class UserManagementEnhancerFactory(private val config: Config) : EnhancerFactory {
    override fun createEnhancers(): List<JerseyResourceEnhancer> = listOf(
        UserManagementEnhancer(config),
        Enhancers.exception,
        Enhancers.ecdsa,
        Enhancers.health,
        Enhancers.radar(AuthConfig(
            jwtRSAPublicKeys = config.jwtRSAPublicKeys,
            jwtResourceName = config.jwtResourceName,
        )),
    )

    class UserManagementEnhancer(private val config: Config): JerseyResourceEnhancer {
        override val classes: Array<Class<*>>  get() {
            return if (config.enableCors) {
                arrayOf(
                    Filters.logResponse,
                    Filters.cors,
                )
            } else {
                arrayOf(
                    Filters.logResponse,
                )
            }
        }

        override val packages: Array<String> = arrayOf(
            "org.radarbase.export.resource",
            "org.radarbase.export.lifecycle",
        )

        override fun AbstractBinder.enhance() {
            bind(config)
                .to(Config::class.java)

            bind(KeycloakClient::class.java)
                .to(KeycloakClient::class.java)
                .`in`(Singleton::class.java)

            bind(UserDataWriter::class.java)
                .to(UserDataWriter::class.java)
                .`in`(Singleton::class.java)

            bind(KeycloakUserManagementService::class.java)
                .to(KeycloakUserManagementService::class.java)
                .`in`(Singleton::class.java)

            bind(ProjectServiceWrapper::class.java)
                .to(ProjectService::class.java)
                .`in`(Singleton::class.java)
        }
    }
}
