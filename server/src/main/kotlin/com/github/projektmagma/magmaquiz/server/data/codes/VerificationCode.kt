package com.github.projektmagma.magmaquiz.server.data.codes

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class VerificationCode(val owner: UserEntity) {
    companion object {
        private val _expirationTime = 2.minutes

        private const val CODE_CHARS = "1234567890ABCDEFGHIJKLMNOPQRSTUWXYZ"
    }

    private lateinit var _code: String

    private val _createdAt: Instant = Clock.System.now()


    fun generateCode(): String {
        val genCode = (1..6).map { CODE_CHARS.random() }.joinToString("")
        _code = BCrypt.hashpw(genCode, BCrypt.gensalt())
        return genCode
    }

    fun isExpired(): Boolean {
        return transaction {
            Clock.System.now() > _createdAt.plus(_expirationTime)
        }

    }


    fun compareCode(rawCode: String): Boolean {
        return BCrypt.checkpw(rawCode, _code)
    }
}