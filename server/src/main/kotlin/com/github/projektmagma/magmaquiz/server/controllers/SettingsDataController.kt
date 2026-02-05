package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeEmailValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeUserNameValue
import io.ktor.http.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SettingsDataController(private val userRepository: UserRepository) {

    fun settingsChangePassword(
        session: UserSession,
        changePasswordValue: ChangePasswordValue
    ): NetworkResource<Unit> {

        val thisUser = userRepository.getUserData(session)

        thisUser.setHashedPassword(changePasswordValue.newPassword)

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
        postContent: ChangeUserNameValue
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (UserEntity.isNameTaken(postContent.newUserName))
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            thisUser.userName = postContent.newUserName
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeEmail(
        session: UserSession,
        postContent: ChangeEmailValue
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (UserEntity.isEmailTaken(postContent.newEmail))
            return NetworkResource.Error(HttpStatusCode.Conflict)


        transaction {
            thisUser.userEmail = postContent.newEmail
        }

        return NetworkResource.Success(Unit)
    }
}