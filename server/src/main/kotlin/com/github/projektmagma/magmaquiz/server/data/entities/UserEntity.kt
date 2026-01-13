package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class UserEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersTable), DomainCapable<User, UserConversionCommand> {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var userPassword by UsersTable.userPassword
        private set
    var userName by UsersTable.userName
    var userEmail by UsersTable.userEmail
    var mustChangePassword by UsersTable.mustChangePassword
    var userBigProfilePicture by UsersTable.userBigProfilePicture
    var userSmallProfilePicture by UsersTable.userSmallProfilePicture
    var lastActivity by UsersTable.lastActivity
    val quizList by QuizEntity referrersOn QuizzesTable.quizCreator

    override fun toDomain(command: UserConversionCommand): User {
        return transaction {
            when (command) {
                UserConversionCommand.ThisUser -> {
                    ThisUser(
                        userId = super.id.value,
                        userName = userName,
                        userEmail = userEmail,
                        mustChangePassword = mustChangePassword,
                        userProfilePicture = userBigProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                    )
                }

                UserConversionCommand.ForeignUserWithSmallPicture -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userSmallProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                    )
                }

                UserConversionCommand.ForeignUserWithBigPicture -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userBigProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                    )
                }
            }
        }

    }

    fun setHashedPassword(password: String) {
        transaction {
            userPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            mustChangePassword = false
        }
    }

    fun checkUserPassword(password: String): Boolean {
        return transaction { BCrypt.checkpw(password, userPassword) }
    }
}
