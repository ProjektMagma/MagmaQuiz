package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersFriendshipsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class UserFriendshipEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersFriendshipsTable) {
    companion object : UUIDEntityClass<UserFriendshipEntity>(UsersFriendshipsTable)

    var userFrom by UserEntity referencedOn UsersFriendshipsTable.userFrom
    var userTo by UserEntity referencedOn UsersFriendshipsTable.userTo
    var wasAccepted by UsersFriendshipsTable.wasAccepted

    fun wasAccepted(): Boolean {
        return transaction { wasAccepted }
    }

    fun setAccepted(value: Boolean) {
        return transaction { wasAccepted = value }
    }
}