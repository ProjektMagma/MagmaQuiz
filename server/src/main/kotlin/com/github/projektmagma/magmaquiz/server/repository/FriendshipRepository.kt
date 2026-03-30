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

    fun userFriendList(user: UserEntity, count: Int, offset: Int, stringToSearch: String): List<UserEntity> {
        return transaction {
            UserFriendshipEntity.find {
                UsersFriendshipsTable.userFrom eq user.id or (UsersFriendshipsTable.userTo eq user.id) and
                        UsersFriendshipsTable.isActive and UsersFriendshipsTable.wasAccepted
            }
                .offset(offset.toLong())
                .limit(count)
                .map {
                    if (it.userTo.id == user.id) it.userFrom
                    else it.userTo
                }
                .filterNot { it.id == user.id }
                .filter { it.userName.contains(stringToSearch, true) }
        }
    }

    fun userInvitations(
        user: UserEntity,
        count: Int,
        offset: Int,
        incoming: Boolean,
        stringToSearch: String
    ): List<UserEntity> {
        return transaction {
            val searchBy = if (incoming) UsersFriendshipsTable.userTo else UsersFriendshipsTable.userFrom

            UserFriendshipEntity.find {
                searchBy eq user.id and
                        UsersFriendshipsTable.isActive and UsersFriendshipsTable.wasAccepted.eq(false)
            }
                .offset(offset.toLong())
                .limit(count)
                .map { if (incoming) it.userFrom else it.userTo }
                .filter { it.userName.contains(stringToSearch, true) }

        }
    }
}