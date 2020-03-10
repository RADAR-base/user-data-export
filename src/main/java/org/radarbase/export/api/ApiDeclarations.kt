package org.radarbase.export.api


data class User(
    var id: String,
    var createdTimestamp: Long,
    var username: String,
    var enabled: Boolean,
    var emailVerified: Boolean,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var attributes: Map<String, List<String>>)
