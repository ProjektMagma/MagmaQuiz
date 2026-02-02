package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.tables.UsersSessionsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class UserSessionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserSessionEntity>(UsersSessionsTable)

    var sessionId by UsersSessionsTable.sessionId
    var sessionValue by UsersSessionsTable.sessionValue
    var sessionOwner by UserEntity referencedOn UsersSessionsTable.sessionOwner
}