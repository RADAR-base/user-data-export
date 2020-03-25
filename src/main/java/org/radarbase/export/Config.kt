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

import org.radarbase.export.inject.UserManagementEnhancerFactory
import org.radarbase.jersey.config.EnhancerFactory
import java.net.URI

data class Config(
        var baseUri: URI = URI.create("http://0.0.0.0:8085/user-management/api/"),
        var resourceConfig: Class<out EnhancerFactory> = UserManagementEnhancerFactory::class.java,
        var keycloakUrl: String = "http://localhost:8080/auth/",
        var clientId: String = "user-data-export",
        var clientSecret: String,
        var realmName: String?,
        var userDataExportFile: String? = "keycloak-user-export.csv",
        var userDataExportPath: String? = "etc/",
        var exportIntervalInMin: Long = 120,
        var jwtResourceName: String = "user-data-manager",
        var jwtRSAPublicKeys: List<String>? = null,
        var enableCors: Boolean? = false
   )
