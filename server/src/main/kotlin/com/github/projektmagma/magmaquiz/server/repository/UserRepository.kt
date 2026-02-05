package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
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

    fun getUsersByName(userName: String? = null, maxCount: Int = 100): List<UserEntity> {
        return transaction {
            if (userName.isNullOrBlank())
                UserEntity.find {
                    UsersTable.isActive eq true
                }
                    .sortedBy { it.lastActivity }
                    .reversed()
                    .take(maxCount)
                    .toList()
            else
                UserEntity.find { UsersTable.userName.lowerCase() like "%${userName.lowercase()}%" and UsersTable.isActive }
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
}