package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersGameHistoryTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class UserGameHistoryEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersGameHistoryTable) {
    companion object : UUIDEntityClass<UserGameHistoryEntity>(UsersGameHistoryTable)

    var quiz by QuizEntity referencedOn UsersGameHistoryTable.quiz
    var user by UserEntity referencedOn UsersGameHistoryTable.user
}