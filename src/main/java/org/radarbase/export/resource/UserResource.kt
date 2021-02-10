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

import org.radarbase.export.service.KeycloakUserManagementService
import org.radarbase.jersey.auth.Auth
import org.radarbase.jersey.auth.Authenticated
import org.radarbase.jersey.auth.NeedsPermission
import org.radarcns.auth.authorization.Permission
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
        @Context private val userManagementService: KeycloakUserManagementService,
        @Context private val auth: Auth) {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{userId}/attributes")
    @NeedsPermission(Permission.Entity.SUBJECT, Permission.Operation.UPDATE)
    fun updateUser(@PathParam("userId") userId: String, @FormParam("projectName") projectName: String): Response {
        logger.info("Request for setting project {} for user {} ...", userId, projectName)
        // this part of feature is not used.
//        return Response.ok(userManagementService.setProjectName(userId, projectName)).build()
        return Response.ok().build()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserResource::class.java)
    }
}
