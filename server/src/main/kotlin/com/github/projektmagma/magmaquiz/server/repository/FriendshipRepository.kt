package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserFriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersFriendshipsTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class FriendshipRepository {
    fun findFriendshipEntityOrNull(firstUser: UserEntity, secondUser: UserEntity): UserFriendshipEntity? {
        return transaction {
            UserFriendshipEntity.find {
                (UsersFriendshipsTable.userFrom eq firstUser.id and (UsersFriendshipsTable.userTo eq secondUser.id)) or
                        (UsersFriendshipsTable.userTo eq firstUser.id and (UsersFriendshipsTable.userFrom eq secondUser.id)) and UsersFriendshipsTable.isActive
            }.firstOrNull()
        }
    }

    fun userFriendList(user: UserEntity): List<UserEntity> {
        return transaction {
            UserFriendshipEntity.find {
                UsersFriendshipsTable.userFrom eq user.id or (UsersFriendshipsTable.userTo eq user.id) and UsersFriendshipsTable.isActive and UsersFriendshipsTable.wasAccepted.eq(
                    true
                )
            }
                .map {
                    if (it.userTo.id == user.id) it.userFrom
                    else it.userTo
                }
                .filterNot { it.id == user.id }
        }
    }

    fun userInvitations(user: UserEntity, incoming: Boolean): List<UserEntity> {
        return transaction {
            if (incoming)
                UserFriendshipEntity.find {
                    UsersFriendshipsTable.userTo eq user.id and
                            UsersFriendshipsTable.isActive.eq(true)
                }.filter { !it.wasAccepted }
                    .map {
                        it.userFrom
                    }
            else
                UserFriendshipEntity.find {
                    UsersFriendshipsTable.userFrom eq user.id and
                            UsersFriendshipsTable.isActive.eq(true)
                }.filter { !it.wasAccepted }
                    .map {
                        it.userTo
                    }
        }
    }
}