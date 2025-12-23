package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FriendshipsTable
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class FriendshipEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, FriendshipsTable) {
    companion object : UUIDEntityClass<FriendshipEntity>(FriendshipsTable)

    var userFrom by UserEntity referencedOn FriendshipsTable.userFrom
    var userTo by UserEntity referencedOn FriendshipsTable.userTo
    var wasAccepted by FriendshipsTable.wasAccepted
}