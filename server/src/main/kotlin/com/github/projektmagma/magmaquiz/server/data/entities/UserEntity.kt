package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtIntEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserEntity(id: EntityID<Int>) : ExtIntEntity(id, UsersTable), DomainCapable<User> {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var userPassword by UsersTable.userPassword
        private set
    var userName by UsersTable.userName
    var userEmail by UsersTable.userEmail
    var mustChangePassword by UsersTable.mustChangePassword
    var userProfilePicture by UsersTable.userProfilePicture

    override fun toDomain(): User {
        return transaction {
            User(
                userId = super.id.value,
                userName = userName,
                userEmail = userEmail,
                mustChangePassword = mustChangePassword,
                userProfilePicture = userProfilePicture
            )
        }
    }

    fun setHashedPassword(password: String) {
        transaction {
            userPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            mustChangePassword = false
        }
    }

    fun checkUserPassword(password: String): Boolean {
        return transaction { BCrypt.checkpw(password, userPassword) }
    }
}
