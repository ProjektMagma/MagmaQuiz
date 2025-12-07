package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.data.domain.Resource
import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.data.rest.values.ChangePasswordValue
import com.github.projektmagma.magmaquiz.data.rest.values.CreateUserValue
import com.github.projektmagma.magmaquiz.data.rest.values.LoginUserValue
import com.github.projektmagma.magmaquiz.server.data.daos.UsersTable
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.http.*
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataController {
    fun tryLoginUser(loginUserValue: LoginUserValue): Resource<User, HttpStatusCode> {
        val dbUsers = transaction {
            UserEntity.find { (UsersTable.userEmail eq loginUserValue.userEmail) }
        }

        val count = transaction { dbUsers.count() }

        if (count == 0L) {
            return Resource.Error(HttpStatusCode.NotFound)
        }

        if (count > 1L) {
            return Resource.Error(HttpStatusCode.MultipleChoices)
        }

        val dbUser = transaction { dbUsers.first() }

        if (!dbUser.checkUserPassword(loginUserValue.userPassword) &&
            !dbUser.userPassword.isNullOrEmpty()
        ) {
            return Resource.Error(HttpStatusCode.Unauthorized)
        }

        val domainUser = transaction { dbUser.toDomain() }

        return Resource.Success(domainUser)
    }

    fun tryRegisterUser(createUserValue: CreateUserValue): Resource<User, HttpStatusCode> {
        val userDoesntExists = transaction {
            UserEntity.find {
                (UsersTable.userEmail eq createUserValue.userEmail) or
                        (UsersTable.userName eq createUserValue.userName)
            }.empty()
        }

        if (!userDoesntExists) {
            return Resource.Error(HttpStatusCode.Conflict)
        }

        val dbUser = transaction {
            UserEntity.new {
                userName = createUserValue.userName
                userEmail = createUserValue.userEmail
            }
        }

        dbUser.setHashedPassword(createUserValue.userPassword)

        return Resource.Success(dbUser.toDomain())
    }

    fun tryChangePassword(
        userSession: UserSession,
        changePasswordValue: ChangePasswordValue
    ): Resource<Unit, HttpStatusCode> {

        val dbUser = transaction {
            UserEntity.findById(userSession.userId)
        }

        if (dbUser == null) {
            return Resource.Error(HttpStatusCode.NotFound)
        }

        dbUser.setHashedPassword(changePasswordValue.newPassword)

        return Resource.Success(Unit)
    }

    fun tryFindUser(userId: Int): Resource<User, HttpStatusCode> {
        val dbUser = transaction {
            UserEntity.findById(userId)
        }

        if (dbUser == null) return Resource.Error(HttpStatusCode.NotFound)

        return Resource.Success(dbUser.toDomain())
    }
}