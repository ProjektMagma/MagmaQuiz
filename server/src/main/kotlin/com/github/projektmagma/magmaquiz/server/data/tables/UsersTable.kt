package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant


object UsersTable : ExtUUIDTable("users", "user_id") {
    val userName = varchar("user_name", 256)
    val userPassword = varchar("user_password", 256).nullable()
    val userEmail = varchar("user_email", 256).uniqueIndex()
    val mustChangePassword = bool("must_change_password").default(false)
    val userBigProfilePicture = binary("user_big_profile_picture").nullable()
    val userSmallProfilePicture = binary("user_small_profile_picture").nullable()
    val lastActivity = timestamp("last_activity").clientDefault { Instant.now() }
    val userBio = text("user_bio").default("")
    val userCountryCode = varchar("user_country_code", 2).default("")
    val userTown = varchar("user_town", 256).default("")
}
