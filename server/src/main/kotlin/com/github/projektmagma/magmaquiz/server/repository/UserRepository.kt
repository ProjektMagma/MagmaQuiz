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
import java.util.*

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

    fun getUsersByName(count: Int, offset: Int, stringToSearch: String): List<UserEntity> {
        return transaction {
            UserEntity.find {
                UsersTable.isActive eq true and (UsersTable.userName.lowerCase() like "%${stringToSearch.lowercase()}%")
            }
                .offset(offset.toLong())
                .limit(count)
                .sortedByDescending { it.lastActivity }
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
}