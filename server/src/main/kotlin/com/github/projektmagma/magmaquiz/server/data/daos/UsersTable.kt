package com.github.projektmagma.magmaquiz.server.data.daos


object UsersTable : ExtIntIdTable("users", "user_id") {
    val userName = varchar("user_name", 256)
    val userPassword = varchar("user_password", 256).nullable()
    val userEmail = varchar("user_email", 256)

}

