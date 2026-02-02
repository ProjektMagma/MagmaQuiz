package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class FavoriteQuizzesEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, FavoriteQuizzesTable) {
    companion object : UUIDEntityClass<FavoriteQuizzesEntity>(FavoriteQuizzesTable)

    var quiz by QuizEntity referencedOn FavoriteQuizzesTable.quiz
    var user by UserEntity referencedOn FavoriteQuizzesTable.user
}