package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import io.ktor.http.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SettingsDataController(private val userRepository: UserRepository) {

    fun settingsChangePassword(
        session: UserSession,
        newPassword: String
    ): NetworkResource<Unit> {

        val thisUser = userRepository.getUserData(session)

        thisUser.setHashedPassword(newPassword)

        return NetworkResource.Success(Unit)
    }


    fun settingsDelete(session: UserSession, isActive: Boolean): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction { thisUser.isActive = isActive }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeProfilePicture(
        session: UserSession,
        postContent: ChangeProfilePictureValue
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction {
            thisUser.userBigProfilePicture = postContent.profilePictureBig
            thisUser.userSmallProfilePicture = postContent.profilePictureSmall
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeUserName(
        session: UserSession,
        newUserName: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (UserEntity.isNameTaken(newUserName))
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            thisUser.userName = newUserName
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeEmail(
        session: UserSession,
        newEmail: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (UserEntity.isEmailTaken(newEmail))
            return NetworkResource.Error(HttpStatusCode.Conflict)


        transaction {
            thisUser.userEmail = newEmail
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeBio(session: UserSession, newBio: String): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction {
            thisUser.userBio = newBio
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeCountryCode(session: UserSession, newCountryCode: String): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction {
            thisUser.userCountryCode = newCountryCode
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeTown(session: UserSession, newTown: String): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction {
            thisUser.userTown = newTown
        }

        return NetworkResource.Success(Unit)
    }
}