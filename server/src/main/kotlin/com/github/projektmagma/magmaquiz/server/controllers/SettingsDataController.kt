package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.codes.VerificationCode
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.mailer.MailTemplates
import com.github.projektmagma.magmaquiz.server.mailer.MailerService
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.ChangeProfilePictureValue
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class SettingsDataController(private val userRepository: UserRepository) {


    private val _verificationCodesList = mutableListOf<VerificationCode>()
    
    private val _passwordResetVerifiedUntil = mutableMapOf<UUID, Instant>()
    private val _passwordResetTtl = 2.minutes
    

    init {
        val healthScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        healthScope.launch {
            while (true) {
                delay(_passwordResetTtl)
                _verificationCodesList.forEach { code ->
                    if (code.isExpired())
                        _verificationCodesList.remove(code)
                }
                _passwordResetVerifiedUntil.entries.removeAll { (_, expiresAt) ->
                    Clock.System.now() > expiresAt
                }
            }
        }
    }

    fun settingsVerifyPasswordResetCode(
        email: String,
        verificationCode: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserByEmail(email.lowercase())
            ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        val codeEntity = _verificationCodesList.firstOrNull { it.owner.id == thisUser.id }
            ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        if (!codeEntity.compareCode(verificationCode)) {
            return NetworkResource.Error(HttpStatusCode.Forbidden)
        }

        _verificationCodesList.remove(codeEntity)
        _passwordResetVerifiedUntil[thisUser.id.value] = Clock.System.now().plus(_passwordResetTtl)

        return NetworkResource.Success(Unit)
    }

    fun settingsChangePasswordAfterVerifiedCode(
        email: String,
        newPassword: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserByEmail(email.lowercase())
            ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        val userId = thisUser.id.value
        val expiresAt = _passwordResetVerifiedUntil[userId]
            ?: return NetworkResource.Error(HttpStatusCode.Forbidden)

        if (Clock.System.now() > expiresAt) {
            _passwordResetVerifiedUntil.remove(userId)
            return NetworkResource.Error(HttpStatusCode.Forbidden)
        }

        thisUser.setHashedPassword(newPassword)
        _passwordResetVerifiedUntil.remove(userId)

        MailerService.sendMail(
            thisUser.userEmail,
            MailTemplates.PasswordChanged,
            Pair("username", thisUser.userName)
        )

        return NetworkResource.Success(Unit)
    }
    
    fun settingsChangePasswordWithOld(
        session: UserSession,
        oldPassword: String,
        newPassword: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (!thisUser.checkUserPassword(oldPassword)) {
            return NetworkResource.Error(HttpStatusCode.Forbidden)
        }

        thisUser.setHashedPassword(newPassword)

        MailerService.sendMail(
            thisUser.userEmail,
            MailTemplates.PasswordChanged,
            Pair("username", thisUser.userName)
        )

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

    fun checkIsEmailTaken(
        newEmail: String
    ): NetworkResource<Unit> {

        if (UserEntity.isEmailTaken(newEmail)) {
            return NetworkResource.Error(HttpStatusCode.Conflict)
        }

        return NetworkResource.Success(Unit)
    }

    fun settingsConfirmEmailChange(
        session: UserSession,
        newEmail: String,
        verificationCode: String
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (UserEntity.isEmailTaken(newEmail)) {
            return NetworkResource.Error(HttpStatusCode.Conflict)
        }

        val codeEntity = _verificationCodesList.firstOrNull { it.owner.id == thisUser.id }
            ?: return NetworkResource.Error(HttpStatusCode.BadRequest)

        if (!codeEntity.compareCode(verificationCode)) {
            return NetworkResource.Error(HttpStatusCode.Forbidden)
        }

        val oldEmail = thisUser.userEmail

        transaction {
            thisUser.userEmail = newEmail
        }
        _verificationCodesList.remove(codeEntity)

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

    fun settingsVerificationCode(email: String): NetworkResource<Unit> {
        val normalizedEmail = email.lowercase()
        val thisUser = userRepository.getUserByEmail(normalizedEmail)
            ?: return NetworkResource.Success(Unit) // nie ujawniasz czy email istnieje

        val verificationCode = _verificationCodesList.firstOrNull { it.owner.id == thisUser.id }
            ?: VerificationCode(thisUser).also { _verificationCodesList.add(it) }

        MailerService.sendMail(
            normalizedEmail,
            MailTemplates.VerificationCode,
            Pair("username", thisUser.userName),
            Pair("verification_code", verificationCode.generateCode())
        )

        return NetworkResource.Success(Unit)
    }
}