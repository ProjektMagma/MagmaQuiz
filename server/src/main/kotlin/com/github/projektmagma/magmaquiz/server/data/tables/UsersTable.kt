package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable


object UsersTable : ExtUUIDTable("users", "user_id") {
    val userName = varchar("user_name", 256)
    val userPassword = varchar("user_password", 256).nullable()
    val userEmail = varchar("user_email", 256)
    val mustChangePassword = bool("must_change_password").default(false)
    val userProfilePicture = binary("user_profile_picture").nullable()
}
