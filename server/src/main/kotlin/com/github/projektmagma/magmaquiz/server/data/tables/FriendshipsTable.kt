package com.github.projektmagma.magmaquiz.server.data.tables

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDTable

object FriendshipsTable : ExtUUIDTable("friendships", "friendship_id") {
    val userFrom = reference("user_from_id", UsersTable)
    val userTo = reference("user_to_id", UsersTable)
    val wasAccepted = bool("was_accepted").default(false)
}