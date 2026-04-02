package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersSessionsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class UserSessionEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersSessionsTable) {
    companion object : UUIDEntityClass<UserSessionEntity>(UsersSessionsTable)

    var sessionKey by UsersSessionsTable.sessionKey
    var sessionValue by UsersSessionsTable.sessionValue
    var sessionOwner by UserEntity referencedOn UsersSessionsTable.sessionOwner
}