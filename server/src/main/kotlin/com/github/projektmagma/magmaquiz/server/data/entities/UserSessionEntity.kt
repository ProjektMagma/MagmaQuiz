package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.tables.UsersSessionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserSessionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserSessionEntity>(UsersSessionsTable)

    var sessionId by UsersSessionsTable.sessionId
    var sessionValue by UsersSessionsTable.sessionValue
    var sessionOwner by UserEntity referencedOn UsersSessionsTable.sessionOwner
}