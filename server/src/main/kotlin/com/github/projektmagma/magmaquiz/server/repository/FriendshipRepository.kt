package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.FriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.FriendshipsTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class FriendshipRepository {
    fun findFriendshipEntityOrNull(firstUser: UserEntity, secondUser: UserEntity): FriendshipEntity? {
        return transaction {
            FriendshipEntity.find {
                (FriendshipsTable.userFrom eq firstUser.id and (FriendshipsTable.userTo eq secondUser.id)) or
                        (FriendshipsTable.userTo eq firstUser.id and (FriendshipsTable.userFrom eq secondUser.id)) and FriendshipsTable.isActive
            }.firstOrNull()
        }
    }

    fun userFriendList(user: UserEntity): List<UserEntity> {
        return transaction {
            FriendshipEntity.find { FriendshipsTable.userFrom eq user.id or (FriendshipsTable.userTo eq user.id) and FriendshipsTable.isActive and FriendshipsTable.wasAccepted }
                .map {
                    if (it.userTo.id == user.id) it.userFrom
                    else it.userTo
                }
                .filterNot { it.id == user.id }
        }
    }

    fun userInvitations(user: UserEntity, incoming: Boolean): List<UserEntity> {
        return transaction {
            FriendshipEntity.find {
                FriendshipsTable.userTo eq user.id and
                        FriendshipsTable.isActive and FriendshipsTable.wasAccepted.eq(false)
            }.map {
                if (incoming)
                    it.userFrom
                else it.userTo
            }
        }
    }
}