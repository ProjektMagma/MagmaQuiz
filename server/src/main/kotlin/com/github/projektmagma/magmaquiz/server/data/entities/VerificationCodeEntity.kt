package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.VerificationCodesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toKotlinInstant

class VerificationCodeEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, VerificationCodesTable) {
    companion object : UUIDEntityClass<VerificationCodeEntity>(VerificationCodesTable) {
        private val _expirationTime = 2.minutes

        private const val CODE_CHARS = "1234567890ABCDEFGHIJKLMNOPQRSTUWXYZ"

        fun tryFindCodeByUser(userEntity: UserEntity): VerificationCodeEntity? {
            return transaction {
                val codeEntity = find { VerificationCodesTable.codeOwner eq userEntity.id }.firstOrNull()
                    ?: return@transaction null

                if (codeEntity.isExpired()) {
                    codeEntity.delete()
                    return@transaction null
                }

                codeEntity
            }
        }
    }

    private var code by VerificationCodesTable.code
    var owner by UserEntity referencedOn VerificationCodesTable.codeOwner

    private fun isExpired(): Boolean {
        return transaction {
            Clock.System.now() > createdAt.toKotlinInstant().plus(_expirationTime)
        }

    }

    fun generateCode(): String {
        val genCode = (1..6).map { CODE_CHARS.random() }.joinToString("")

        transaction { code = BCrypt.hashpw(genCode, BCrypt.gensalt()) }

        return genCode
    }

    fun compareCode(rawCode: String): Boolean {
        return BCrypt.checkpw(rawCode, code)
    }
}