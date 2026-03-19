package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersFavoriteQuizzesTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class UserFavoriteQuizzesEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersFavoriteQuizzesTable) {
    companion object : UUIDEntityClass<UserFavoriteQuizzesEntity>(UsersFavoriteQuizzesTable) {
        fun isFavoriteByUser(quizEntity: QuizEntity, userEntity: UserEntity): Boolean {
            return transaction {
                find { UsersFavoriteQuizzesTable.user eq userEntity.id and (UsersFavoriteQuizzesTable.quiz eq quizEntity.id) and (UsersFavoriteQuizzesTable.isActive eq true) }
                    .any()
            }
        }
    }

    var quiz by QuizEntity referencedOn UsersFavoriteQuizzesTable.quiz
    var user by UserEntity referencedOn UsersFavoriteQuizzesTable.user
}