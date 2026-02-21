package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersFavoriteQuizzesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class UserFavoriteQuizzesEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersFavoriteQuizzesTable) {
    companion object : UUIDEntityClass<UserFavoriteQuizzesEntity>(UsersFavoriteQuizzesTable)

    var quiz by QuizEntity referencedOn UsersFavoriteQuizzesTable.quiz
    var user by UserEntity referencedOn UsersFavoriteQuizzesTable.user
}