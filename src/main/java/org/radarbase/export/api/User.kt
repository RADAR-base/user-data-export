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

package org.radarbase.export.api

import com.fasterxml.jackson.annotation.JsonIgnore

data class User(
    var id: String,
    var createdTimestamp: Long,
    var username: String,
    var enabled: Boolean,
    var emailVerified: Boolean,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var attributes: Map<String, List<String>>?) {

    fun toMap(): Map<String, String> {
        val userMap = mutableMapOf(
                "id" to id,
                "username" to username,
                "email" to email.orEmpty(),
                "firstName" to firstName.orEmpty(),
                "lastName" to lastName.orEmpty(),
                "createdTimestamp" to createdTimestamp.toString(),
                "enabled" to enabled.toString(),
                "emailVerified" to enabled.toString(),
                "enabled" to enabled.toString()
        )
        attributes?.map { entry ->
            // keycloak returns the value as a list although it only allows one value per key.
            // Thus fetching the first item here.
            userMap[entry.key] = entry.value.first()
        }
        return userMap
    }

    fun reset(): User = this.copy(
            firstName = "",
            lastName = "",
            attributes = mutableMapOf(IS_PROCESSED to listOf(true.toString())))

    @JsonIgnore
    fun isProcessed(): Boolean {
        return (attributes?.getOrDefault(IS_PROCESSED, null)?.first()?.toBoolean() ?: false)
    }

    companion object {
        const val IS_PROCESSED = "isProcessed"

    }
}
