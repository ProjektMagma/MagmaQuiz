package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.FriendshipsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersTable), DomainCapable<User, UserConversionCommand> {
    companion object : UUIDEntityClass<UserEntity>(UsersTable) {

        fun isNameTaken(name: String): Boolean {
            return transaction { find { UsersTable.userName eq name }.firstOrNull() != null }
        }

        fun isEmailTaken(email: String): Boolean {
            return transaction { find { UsersTable.userEmail eq email }.firstOrNull() != null }
        }
    }

    var userPassword by UsersTable.userPassword
        private set
    var userName by UsersTable.userName
    var userEmail by UsersTable.userEmail
    var mustChangePassword by UsersTable.mustChangePassword
    var userBigProfilePicture by UsersTable.userBigProfilePicture
    var userSmallProfilePicture by UsersTable.userSmallProfilePicture
    var lastActivity by UsersTable.lastActivity
    private val quizList by QuizEntity referrersOn QuizzesTable.quizCreator

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

                is UserConversionCommand.ForeignUserWithSmallPicture -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userSmallProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                        friendshipStatus = checkFriendship(command.caller),
                    )
                }

                is UserConversionCommand.ForeignUserWithBigPicture -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userBigProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                        friendshipStatus = checkFriendship(command.caller),
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

    private fun checkFriendship(otherUser: UserEntity): FriendshipStatus {
        return transaction {
            val friendship = FriendshipEntity.find {
                (FriendshipsTable.userFrom eq otherUser.id and (FriendshipsTable.userTo eq this@UserEntity.id)) or
                        (FriendshipsTable.userTo eq otherUser.id and (FriendshipsTable.userFrom eq this@UserEntity.id)) and FriendshipsTable.isActive
            }.firstOrNull()

            if (friendship == null) return@transaction FriendshipStatus.None
            else if (friendship.wasAccepted) return@transaction FriendshipStatus.Friends
            else if (friendship.userTo.id == otherUser.id) return@transaction FriendshipStatus.Incoming
            else return@transaction FriendshipStatus.Outgoing
        }
    }


    fun favoriteQuizzes(): List<QuizEntity> {
        return transaction {
            val favorites =
                FavoriteQuizzesEntity.find {
                    FavoriteQuizzesTable.user eq this@UserEntity.id and
                            (FavoriteQuizzesTable.isActive)
                }
                    .map { it.quiz.id }
            QuizEntity.find {
                QuizzesTable.isActive eq true and
                        (QuizzesTable.isPublic eq true or (QuizzesTable.quizCreator eq this@UserEntity.id)) and
                        (QuizzesTable.id inList (favorites))
            }.toList()
        }
    }

    fun getUserQuizzes(): List<QuizEntity> {
        return transaction { quizList.toList() }
    }
}
