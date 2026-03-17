package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.QuizReviewEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesReviewsTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

class UserRepository {

    fun getUserData(userId: UUID): UserEntity? {
        val user = transaction {
            val it = UserEntity.findById(userId)
            if (it != null && it.isActive) it
            else null
        }
        return user
    }

    fun getUserData(session: UserSession) = getUserData(session.userId)!!

    fun getUsersByName(count: Int, stringToSearch: String?): List<UserEntity> {
        return transaction {
            if (stringToSearch.isNullOrBlank())
                UserEntity.find {
                    UsersTable.isActive eq true
                }
                    .sortedBy { it.lastActivity }
                    .reversed()
                    .take(count)
                    .toList()
            else
                UserEntity.find { UsersTable.userName.lowerCase() like "%${stringToSearch.lowercase()}%" and UsersTable.isActive }
                    .toList()
        }
    }

    fun getUserByEmail(email: String): UserEntity? {
        return transaction {
            UserEntity.find {
                UsersTable.userEmail eq email and
                        UsersTable.isActive.eq(true)
            }.firstOrNull()
        }
    }

    fun getUserReviews(userEntity: UserEntity): List<QuizReviewEntity> {
        return transaction {
            QuizReviewEntity.find {
                QuizzesReviewsTable.author eq userEntity.id
            }.toList()
        }
    }

    fun hasUserReviewForQuiz(userId: UUID, quizId: UUID): Boolean = transaction {
        QuizReviewEntity.find {
            (QuizzesReviewsTable.author eq userId) and
                    (QuizzesReviewsTable.quiz eq quizId)
        }.any()
    }
}