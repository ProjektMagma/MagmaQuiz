package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.UserFriendshipEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.FriendshipRepository
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import io.ktor.http.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class UsersDataController(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) {

    /**
     * Wyszukuje użytkowników po nazwie użytkownika.
     *
     * @param userName Nazwa użytkownika do wyszukania (opcjonalnie)
     * @return NetworkResource z listą użytkowników spełniających kryteria
     * (`206 Partial Content` jeśli lista jest ograniczona, `200 OK` w pozostałych przypadkach)
     */
    fun usersFindByUserName(session: UserSession, userName: String? = null): NetworkResource<List<User>> {
        val thisUser = userRepository.getUserData(session)
        val userList = userRepository.getUsersByName(userName)
        val usersMapped =
            userList.filter { it.id != thisUser.id }
                .map { it.toDomain(UserConversionCommand.ForeignUser(thisUser)) }

        return NetworkResource.Success(usersMapped, HttpStatusCode.PartialContent)
    }

    /**
     * Pobiera dane użytkownika na podstawie jego identyfikatora.
     *
     * @param userId Identyfikator użytkownika
     * @return NetworkResource z danymi użytkownika (`200 OK`) lub błąd, jeśli nie znaleziono (`404 Not Found`)
     */
    fun usersUserData(session: UserSession, userId: UUID): NetworkResource<User> {
        val thisUser = userRepository.getUserData(session)
        val userToFind = userRepository.getUserData(userId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        return NetworkResource.Success(userToFind.toDomain(UserConversionCommand.ForeignUserWithData(thisUser)))
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
     * @return NetworkResource z wynikiem operacji (`200 OK`, `400 Bad Request`, `404 Not Found`)
     * `True` - wysłano zaproszenie
     * `False` - anulowano zaproszenie
     */
    fun usersFriendshipSendInvitation(session: UserSession, userId: UUID): NetworkResource<Boolean> {

        if (session.userId == userId)
            return NetworkResource.Error(HttpStatusCode.BadRequest)

        val userFrom = userRepository.getUserData(session)
        val userTo = userRepository.getUserData(userId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        val friendship = friendshipRepository.findFriendshipEntityOrNull(userFrom, userTo)

        if (friendship != null) {
            friendship.setIsActive(false)
            return NetworkResource.Success(false)
        } else
            transaction {
                UserFriendshipEntity.new {
                    this.userFrom = userFrom
                    this.userTo = userTo
                }
            }

        return NetworkResource.Success(true)
    }


    /**
     * Przełącza akceptowanie lub anulowanie zaproszenia do znajomych.
     * - Zwraca `201`, jeśli zaproszenie zostało zaakceptowane.
     * - Zwraca `200`, jeśli zaproszenie zostało anulowane.
     * - Zwraca `404`, jeśli żadne zaproszenie lub użytkownik nie istnieje.
     *
     * @param session Sesja użytkownika wykonującego akcję
     * @param userId Identyfikator użytkownika, którego dotyczy zaproszenie
     * @return NetworkResource z wynikiem operacji (`200 OK`, `404 Not Found`)
     * `True` - potwierdzono znajomość
     * `False` - usunięto istniejącą znajomość
     */
    fun usersFriendshipAcceptInvitation(session: UserSession, userId: UUID): NetworkResource<Boolean> {
        val userFrom = userRepository.getUserData(session)
        val userTo = userRepository.getUserData(userId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        val friendship =
            friendshipRepository.findFriendshipEntityOrNull(userFrom, userTo) ?: return NetworkResource.Error(
                HttpStatusCode.NotFound
            )

        if (friendship.wasAccepted()) {
            friendship.setIsActive(false)
            return NetworkResource.Success(false)
        }

        friendship.setAccepted(true)

        return NetworkResource.Success(true)
    }

    /**
     * Pobiera listę zaakceptowanych znajomych użytkownika.
     *
     * @param session Sesja użytkownika
     * @return NetworkResource z listą znajomych (`200 OK`)
     */
    fun usersFriendshipFriendList(session: UserSession): NetworkResource<List<User>> {
        val thisUser = userRepository.getUserData(session)

        val friendList = friendshipRepository.userFriendList(thisUser)
            .map {
                it.toDomain(UserConversionCommand.ForeignUser(thisUser))
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
        val thisUser = userRepository.getUserData(session)

        val friendList = friendshipRepository.userInvitations(thisUser, true)
            .map {
                it.toDomain(UserConversionCommand.ForeignUser(thisUser))
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
        val thisUser = userRepository.getUserData(session)

        val friendList = friendshipRepository.userInvitations(thisUser, false)
            .map {
                it.toDomain(UserConversionCommand.ForeignUser(thisUser))
            }

        return NetworkResource.Success(friendList)
    }
}