package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.User
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.shared.data.rest.values.LoginUserValue
import io.ktor.http.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class AuthDataController(private val userRepository: UserRepository) {
    fun authLogin(loginUserValue: LoginUserValue): NetworkResource<User> {
        val thisUser = userRepository.getUserByEmail(loginUserValue.userEmail) ?: return NetworkResource.Error(
            HttpStatusCode.NotFound
        )

        // TODO: TO TRZEBA W KOŃCU ZMIENIĆ 
        val isPasswordEmpty = thisUser.userPassword.isNullOrEmpty()

        if (isPasswordEmpty) {
            transaction { thisUser.mustChangePassword = true }
        } else if (!thisUser.checkUserPassword(loginUserValue.userPassword)
        ) {
            return NetworkResource.Error(HttpStatusCode.Unauthorized)
        }

        return NetworkResource.Success(thisUser.toDomain(UserConversionCommand.ThisUser))
    }

    fun authRegister(createUserValue: CreateUserValue): NetworkResource<User> {
        if (UserEntity.isEmailTaken(createUserValue.userEmail))
            return NetworkResource.Error(HttpStatusCode.Conflict)


        val dbUser = transaction {
            UserEntity.new {
                userName = createUserValue.userName
                userEmail = createUserValue.userEmail.lowercase()
            }
        }

        dbUser.setHashedPassword(createUserValue.userPassword)

        return NetworkResource.Success(dbUser.toDomain(UserConversionCommand.ThisUser), HttpStatusCode.Created)
    }


    fun authWhoami(session: UserSession): NetworkResource<User> {
        val dbUser = userRepository.getUserData(session)
        return NetworkResource.Success(dbUser.toDomain(UserConversionCommand.ThisUser))
    }
}