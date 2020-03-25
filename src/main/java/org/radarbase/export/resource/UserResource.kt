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

package org.radarbase.export.resource

import org.radarbase.export.keycloak.KeycloakClient
import org.radarbase.jersey.auth.Authenticated
import org.slf4j.LoggerFactory
import javax.annotation.Resource
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("users")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Resource
@Singleton
class UserResource(
        @Context private val keycloakClient: KeycloakClient) {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{userId}/attributes")
    fun updateUser(@PathParam("userId") userId: String, @FormParam("projectName") projectName: String): Response {
//        return keycloakClient.
        logger.info("user {} and project {}", userId, projectName)
        return Response.ok().build()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserResource::class.java)
    }
}
