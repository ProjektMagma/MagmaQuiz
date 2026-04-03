package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.entities.VerificationCodeEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.mailer.MailTemplates
import com.github.projektmagma.magmaquiz.server.mailer.MailerService
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import io.ktor.http.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SettingsDataController(private val userRepository: UserRepository) {

    fun settingsChangePassword(
        session: UserSession,
        newPassword: String,
        verificationCode: String
    ): NetworkResource<Unit> {

        val thisUser = userRepository.getUserData(session)

        val codeEntity =
            VerificationCodeEntity.tryFindCodeByUser(thisUser)
                ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        if (!codeEntity.compareCode(verificationCode))
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        thisUser.setHashedPassword(newPassword)

        MailerService.sendMail(thisUser.userEmail, MailTemplates.PasswordChanged, Pair("username", thisUser.userName))

        return NetworkResource.Success(Unit)
    }


    fun settingsDelete(session: UserSession, isActive: Boolean): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        transaction { thisUser.isActive = isActive }

        MailerService.sendMail(thisUser.userEmail, MailTemplates.AccountDeleted, Pair("username", thisUser.userName))

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
        newEmail: String,
        verificationCode: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        val oldEmail = thisUser.userEmail

        if (UserEntity.isEmailTaken(newEmail))
            return NetworkResource.Error(HttpStatusCode.Conflict)


        val codeEntity =
            VerificationCodeEntity.tryFindCodeByUser(thisUser)
                ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        if (!codeEntity.compareCode(verificationCode))
            return NetworkResource.Error(HttpStatusCode.BadRequest)

        transaction {
            thisUser.userEmail = newEmail
        }

        MailerService.sendMail(oldEmail, MailTemplates.EmailChanged, Pair("username", thisUser.userName))

        MailerService.sendMail(newEmail, MailTemplates.EmailChanged, Pair("username", thisUser.userName))

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

    fun settingsVerificationCode(session: UserSession): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        val verificationCode = VerificationCodeEntity.tryFindCodeByUser(thisUser)
            ?: transaction { VerificationCodeEntity.new { owner = thisUser } }

        val generatedCode = verificationCode.generateCode()

        MailerService.sendMail(
            thisUser.userEmail,
            MailTemplates.VerificationCode,
            Pair("username", thisUser.userName),
            Pair("verification_code", generatedCode)
        )

        return NetworkResource.Success(Unit)
    }
}