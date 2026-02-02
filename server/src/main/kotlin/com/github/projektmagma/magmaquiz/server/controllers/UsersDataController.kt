package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.controllers.util.friendshipEntityOrNull
import com.github.projektmagma.magmaquiz.server.controllers.util.friendships
import com.github.projektmagma.magmaquiz.server.controllers.util.isUserActive
import com.github.projektmagma.magmaquiz.server.controllers.util.toUserList
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.FriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import io.ktor.http.*
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class UsersDataController {

    /**
     * Wyszukuje użytkowników po nazwie użytkownika.
     *
     * @param userName Nazwa użytkownika do wyszukania (opcjonalnie)
     * @return NetworkResource z listą użytkowników spełniających kryteria
     * (`206 Partial Content` jeśli lista jest ograniczona, `200 OK` w pozostałych przypadkach)
     */
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

    /**
     * Pobiera dane użytkownika na podstawie jego identyfikatora.
     *
     * @param userId Identyfikator użytkownika
     * @return NetworkResource z danymi użytkownika (`200 OK`) lub błąd, jeśli nie znaleziono (`404 Not Found`)
     */
    fun usersUserData(userId: UUID): NetworkResource<User> {
        if (!isUserActive(userId))
            return NetworkResource.Error(HttpStatusCode.NotFound)

        val dbUser = transaction {
            UserEntity.findById(userId)
        }!!

        return NetworkResource.Success(transaction {
            dbUser.toDomain(UserConversionCommand.ForeignUserWithBigPicture)
        })
    }

    /**
     * Przełącza wysyłanie lub cofanie zaproszenia do znajomych.
     * - Zwraca `201`, jeśli zaproszenie zostało wysłane.
     * - Zwraca `200`, jeśli zaproszenie zostało cofnięte.
     * - Zwraca `400`, jeśli użytkownik próbuje zaprosić samego siebie.
     * - Zwraca `404`, jeśli użytkownik nie istnieje.
     *
     * @param session Sesja użytkownika wykonującego akcję
     * @param userId Identyfikator użytkownika, do którego wysyłane jest zaproszenie
     * @return NetworkResource z wynikiem operacji (`201 Created`, `200 OK`, `400 Bad Request`, `404 Not Found`)
     */
    fun usersFriendshipSendInvitation(session: UserSession, userId: UUID): NetworkResource<Unit> {

        if (session.userId == userId)
            return NetworkResource.Error(HttpStatusCode.BadRequest)

        if (!isUserActive(userId))
            return NetworkResource.Error(HttpStatusCode.NotFound)

        val dbUser = transaction {
            UserEntity.findById(userId)
        }!!

        val friendship = transaction { UserEntity.findById(session.userId) }!!.friendshipEntityOrNull(userId)

        if (friendship != null && transaction { friendship.isActive }) {
            transaction { friendship.isActive = false }
            return NetworkResource.Success(Unit)
        }

        transaction {
            FriendshipEntity.new {
                userFrom = UserEntity.findById(session.userId)!!
                userTo = dbUser
            }
        }

        return NetworkResource.Success(Unit, HttpStatusCode.Created)
    }


    /**
     * Przełącza akceptowanie lub anulowanie zaproszenia do znajomych.
     * - Zwraca `201`, jeśli zaproszenie zostało zaakceptowane.
     * - Zwraca `200`, jeśli zaproszenie zostało anulowane.
     * - Zwraca `404`, jeśli żadne zaproszenie lub użytkownik nie istnieje.
     *
     * @param session Sesja użytkownika wykonującego akcję
     * @param userId Identyfikator użytkownika, którego dotyczy zaproszenie
     * @return NetworkResource z wynikiem operacji (`201 Created`, `200 OK`, `404 Not Found`)
     */
    fun usersFriendshipAcceptInvitation(session: UserSession, userId: UUID): NetworkResource<Unit> {
        val friendship =
            transaction { UserEntity.findById(session.userId) }!!.friendshipEntityOrNull(userId)

        if (friendship == null || !isUserActive(userId))
            return NetworkResource.Error(HttpStatusCode.NotFound)

        if (transaction { friendship.isActive && friendship.wasAccepted }) {
            transaction { friendship.isActive = false }
            return NetworkResource.Success(Unit)
        }

        transaction { friendship.wasAccepted = true }

        return NetworkResource.Success(Unit, HttpStatusCode.Created)
    }

    /**
     * Pobiera listę zaakceptowanych znajomych użytkownika.
     *
     * @param session Sesja użytkownika
     * @return NetworkResource z listą znajomych (`200 OK`)
     */
    fun usersFriendshipFriendList(session: UserSession): NetworkResource<List<User>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)!!
        }

        val friendList = transaction {
            dbUser.friendships(true)
                .toUserList(dbUser)
                .map {
                    it.toDomain(UserConversionCommand.ForeignUserWithSmallPicture)
                }
        }

        return NetworkResource.Success(friendList)
    }

    /**
     * Pobiera listę przychodzących zaproszeń do znajomych.
     *
     * @param session Sesja użytkownika
     * @return NetworkResource z listą użytkowników, którzy wysłali zaproszenia (`200 OK`)
     */
    fun usersFriendshipIncoming(session: UserSession): NetworkResource<List<User>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)!!
        }

        val friendList = transaction {
            dbUser.friendships(false)
                .filter { it.userTo.id == dbUser.id }
                .toUserList(dbUser)
                .map {
                    it.toDomain(UserConversionCommand.ForeignUserWithSmallPicture)
                }
        }

        return NetworkResource.Success(friendList)
    }


    /**
     * Pobiera listę wychodzących zaproszeń do znajomych.
     *
     * @param session Sesja użytkownika
     * @return NetworkResource z listą użytkowników, do których wysłano zaproszenia (`200 OK`)
     */
    fun usersFriendshipOutgoing(session: UserSession): NetworkResource<List<User>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)!!
        }

        val friendList = transaction {
            dbUser.friendships(false)
                .filter { it.userFrom.id == dbUser.id }
                .toUserList(dbUser)
                .map {
                    it.toDomain(UserConversionCommand.ForeignUserWithSmallPicture)
                }
        }

        return NetworkResource.Success(friendList)
    }
}