package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable


object UsersSessionsTable : ExtUUIDTable("users_sessions", "session_id") {
    val sessionKey = varchar("session_key", 255)
    val sessionValue = varchar("session_value", 255)
    val sessionOwner = reference("session_owner_id", UsersTable)
}