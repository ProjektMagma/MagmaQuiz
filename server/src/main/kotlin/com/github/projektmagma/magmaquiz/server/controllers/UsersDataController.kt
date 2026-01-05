package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.FriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FriendshipsTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import io.ktor.http.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UsersDataController {
    fun usersFindByUserName(userName: String? = null): NetworkResource<List<User>> {
        val userList = transaction {
            if (userName.isNullOrBlank())
                UserEntity.find {
                    UsersTable.isActive eq true
                }
                    .sortedBy { it.createdAt }
                    .reversed()
                    .take(100)
            else
                UserEntity
                    .find { UsersTable.userName.lowerCase() like "%${userName.lowercase()}%" and UsersTable.isActive }
        }
        val usersMapped =
            transaction { userList.map { it.toDomain(UserConversionCommand.ForeignUserWithSmallPicture) } }

        return NetworkResource.Success(usersMapped, HttpStatusCode.PartialContent)
    }

    fun usersUserData(userId: UUID): NetworkResource<User> {
        val dbUser = transaction {
            UserEntity.findById(userId)
        }

        if (dbUser == null || transaction { !dbUser.isActive })
            return NetworkResource.Error(HttpStatusCode.NotFound)

        return NetworkResource.Success(transaction {
            dbUser.toDomain(UserConversionCommand.ForeignUserWithBigPicture)
        })
    }

    fun usersFriendshipSendInvitation(session: UserSession, userId: UUID): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(userId)
        }

        if (dbUser == null || transaction { !dbUser.isActive })
            return NetworkResource.Error(HttpStatusCode.NotFound)

        val friendship = transaction {
            FriendshipEntity.find {
                (FriendshipsTable.userFrom eq session.userId and (FriendshipsTable.userTo eq dbUser.id)) or
                        (FriendshipsTable.userFrom eq dbUser.id and (FriendshipsTable.userTo eq session.userId))
            }.firstOrNull()
        }

        if (friendship != null && transaction { dbUser.isActive })
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            FriendshipEntity.new {
                userFrom = dbUser
                userTo = UserEntity.findById(session.userId)!!
            }
        }

        return NetworkResource.Success(Unit)
    }

    fun usersFriendshipAcceptInvitation(session: UserSession, userId: UUID): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(userId)
        }

        if (dbUser == null || transaction { !dbUser.isActive })
            return NetworkResource.Error(HttpStatusCode.NotFound)

        val friendship = transaction {
            FriendshipEntity.find {
                (FriendshipsTable.userFrom eq session.userId and (FriendshipsTable.userTo eq dbUser.id)) or
                        (FriendshipsTable.userFrom eq dbUser.id and (FriendshipsTable.userTo eq session.userId))
            }.firstOrNull()
        }

        if (friendship == null || transaction { !dbUser.isActive })
            return NetworkResource.Error(HttpStatusCode.NotFound)

        transaction { friendship.wasAccepted = true }

        return NetworkResource.Success(Unit)
    }

    fun usersFriendshipFriendList(session: UserSession): NetworkResource<List<User>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)!!
        }

        val friendshipList = transaction {
            FriendshipEntity.find {
                FriendshipsTable.userFrom eq dbUser.id or (FriendshipsTable.userTo eq dbUser.id) and FriendshipsTable.isActive
            }
        }

        val friendList = transaction {
            friendshipList
                .map { friendship ->
                    if (friendship.userTo == dbUser) friendship.userFrom
                    else friendship.userTo
                }.map {
                    it.toDomain(UserConversionCommand.ForeignUserWithSmallPicture)
                }
        }

        return NetworkResource.Success(friendList)
    }
}