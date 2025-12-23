package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.http.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class AuthDataController {
    fun authLogin(loginUserValue: LoginUserValue): NetworkResource<User> {
        val dbUsers = transaction {
            UserEntity.find {
                (UsersTable.userEmail.lowerCase() eq loginUserValue.userEmail.lowercase() and
                        UsersTable.isActive)
            }
        }

        val count = transaction { dbUsers.count() }

        if (count == 0L) {
            return NetworkResource.Error(HttpStatusCode.NotFound)
        }

        if (count > 1L) {
            return NetworkResource.Error(HttpStatusCode.MultipleChoices)
        }

        val dbUser = transaction { dbUsers.first() }
        val isPasswordEmpty = dbUser.userPassword.isNullOrEmpty()

        if (isPasswordEmpty) {
            transaction { dbUser.mustChangePassword = true }
        } else if (!dbUser.checkUserPassword(loginUserValue.userPassword)
        ) {
            return NetworkResource.Error(HttpStatusCode.Unauthorized)
        }


        val domainUser = transaction { dbUser.toDomain() }

        return NetworkResource.Success(domainUser)
    }

    fun authRegister(createUserValue: CreateUserValue): NetworkResource<User> {
        val userDoesntExists = transaction {
            UserEntity.find {
                (UsersTable.userEmail.lowerCase() eq createUserValue.userEmail.lowercase() or
                        (UsersTable.userName eq createUserValue.userName)) and
                        (UsersTable.isActive)
            }.empty()
        }

        if (!userDoesntExists) {
            return NetworkResource.Error(HttpStatusCode.Conflict)
        }

        val dbUser = transaction {
            UserEntity.new {
                userName = createUserValue.userName
                userEmail = createUserValue.userEmail.lowercase()
            }
        }

        dbUser.setHashedPassword(createUserValue.userPassword)

        return NetworkResource.Success(dbUser.toDomain(), HttpStatusCode.Created)
    }


    fun authWhoami(session: UserSession): NetworkResource<User> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }

        if (dbUser == null) return NetworkResource.Error(HttpStatusCode.NotFound)

        return NetworkResource.Success(dbUser.toDomain())
    }
}