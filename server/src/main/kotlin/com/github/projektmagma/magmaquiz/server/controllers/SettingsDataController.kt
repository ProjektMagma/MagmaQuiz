package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeEmailValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeUserNameValue
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction

class SettingsDataController {

    fun settingsChangePassword(
        session: UserSession,
        changePasswordValue: ChangePasswordValue
    ): NetworkResource<Unit> {

        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        dbUser.setHashedPassword(changePasswordValue.newPassword)

        return NetworkResource.Success(Unit)
    }


    fun settingsDelete(session: UserSession, isActive: Boolean): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!


        transaction { dbUser.isActive = isActive }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeProfilePicture(
        session: UserSession,
        postContent: ChangeProfilePictureValue
    ): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        transaction {
            dbUser.userBigProfilePicture = postContent.profilePictureBig
            dbUser.userSmallProfilePicture = postContent.profilePictureSmall
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeUserName(
        session: UserSession,
        postContent: ChangeUserNameValue
    ): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val otherDbUser = transaction {
            UserEntity.find { UsersTable.userName eq postContent.newUserName }.firstOrNull()
        }

        if (otherDbUser != null)
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            dbUser.userName = postContent.newUserName
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsChangeEmail(
        session: UserSession,
        postContent: ChangeEmailValue
    ): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val otherDbUser = transaction {
            UserEntity.find { UsersTable.userEmail eq postContent.newEmail }.firstOrNull()
        }

        if (otherDbUser != null)
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            dbUser.userEmail = postContent.newEmail
        }

        return NetworkResource.Success(Unit)
    }
}