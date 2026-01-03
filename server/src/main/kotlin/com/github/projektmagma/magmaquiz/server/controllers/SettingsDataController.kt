package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ImageValue
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

        return NetworkResource.Success(Unit, HttpStatusCode.Found)
    }

    fun settingsChangeProfilePicture(session: UserSession, postContent: ImageValue): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        transaction { dbUser.userProfilePicture = postContent.image }

        return NetworkResource.Success(Unit)
    }
}