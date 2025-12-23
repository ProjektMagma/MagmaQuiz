package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class FavoriteQuizzesEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, FavoriteQuizzesTable) {
    companion object : UUIDEntityClass<FavoriteQuizzesEntity>(FavoriteQuizzesTable)

    var quiz by QuizEntity referencedOn FavoriteQuizzesTable.quiz
    var user by UserEntity referencedOn FavoriteQuizzesTable.user
}