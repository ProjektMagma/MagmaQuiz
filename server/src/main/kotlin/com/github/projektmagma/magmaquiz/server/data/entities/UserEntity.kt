package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.*
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class UserEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersTable), DomainCapable<User, UserConversionCommand> {
    companion object : UUIDEntityClass<UserEntity>(UsersTable) {

        fun isNameTaken(name: String): Boolean {
            return transaction { find { UsersTable.userName.lowerCase() eq name.lowercase() }.firstOrNull() != null }
        }

        fun isEmailTaken(email: String): Boolean {
            return transaction { find { UsersTable.userEmail.lowerCase() eq email.lowercase() }.firstOrNull() != null }
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
    var userBio by UsersTable.userBio
    var userCountryCode by UsersTable.userCountryCode
    var userTown by UsersTable.userTown

    private val quizList by QuizEntity referrersOn QuizzesTable.quizCreator
    private val playHistoryList by UserGameHistoryEntity referrersOn UsersGameHistoryTable.user
    private val favoriteQuizzesList by QuizEntity via UsersFavoriteQuizzesTable

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
                        userBio = userBio,
                        userCountryCode = userCountryCode,
                        userTown = userTown,
                    )
                }

                is UserConversionCommand.ForeignUser -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userSmallProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                        userBio = "",
                        userCountryCode = "",
                        userTown = "",
                        friendshipStatus =
                            if (command.caller == null)
                                FriendshipStatus.Unknown
                            else
                                checkFriendship(command.caller),
                    )
                }

                is UserConversionCommand.ForeignUserWithData -> {
                    ForeignUser(
                        userId = super.id.value,
                        userName = userName,
                        userProfilePicture = userBigProfilePicture,
                        createdAt = createdAt.epochSecond,
                        lastActivity = lastActivity.epochSecond,
                        userBio = userBio,
                        userCountryCode = userCountryCode,
                        userTown = userTown,
                        friendshipStatus = checkFriendship(command.caller),
                    )
                }
            }
        }

    }

    private fun checkFriendship(otherUser: UserEntity): FriendshipStatus {
        return transaction {
            val friendship = UserFriendshipEntity.find {
                (UsersFriendshipsTable.userFrom eq otherUser.id and (UsersFriendshipsTable.userTo eq this@UserEntity.id)) or
                        (UsersFriendshipsTable.userTo eq otherUser.id and (UsersFriendshipsTable.userFrom eq this@UserEntity.id)) and UsersFriendshipsTable.isActive
            }.firstOrNull()

            if (friendship == null) return@transaction FriendshipStatus.None
            else if (friendship.wasAccepted) return@transaction FriendshipStatus.Friends
            else if (friendship.userTo.id == otherUser.id) return@transaction FriendshipStatus.Incoming
            else return@transaction FriendshipStatus.Outgoing
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


    fun favoriteQuizzes(count: Int): List<QuizEntity> {
        return transaction {
            favoriteQuizzesList
                .filter { it.isActive && it.isPublic || it.isUserCreator(this@UserEntity) }
                .sortedBy { it.likesCount }
                .toList()
                .take(count)
        }
    }

    fun getUserQuizzes(count: Int): List<QuizEntity> {
        return transaction {
            quizList
                .filter { it.isActive && it.isPublic || it.isUserCreator(this@UserEntity) }
                .sortedBy { it.likesCount }
                .toList()
                .take(count)
        }
    }

    fun getLastPlayedQuizzes(count: Int): List<QuizEntity> {
        // TODO: Sprawdzić dobrze czy działa
        return transaction {
            playHistoryList
                .sortedBy { it.createdAt }
                .reversed()
                .map { it.quiz }
                .filter { it.isActive && it.isPublic || it.isUserCreator(this@UserEntity) }
                .take(count)
        }
    }
}
