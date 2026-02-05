package com.github.projektmagma.magmaquiz.server.storage

import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserSessionEntity
import com.github.projektmagma.magmaquiz.server.data.tables.UsersSessionsTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class ExposedSessionStorage : SessionStorage {
    override suspend fun write(id: String, value: String) {
        val userSession = Json.decodeFromString<UserSession>(value)
        transaction {
            UserSessionEntity.new {
                sessionId = id
                sessionValue = value
                sessionOwner = UserEntity.findById(userSession.userId)!!
            }
        }
    }

    override suspend fun invalidate(id: String) {
        transaction {
            UserSessionEntity.find { UsersSessionsTable.sessionId eq id }.firstOrNull()?.delete()
        }
    }

    override suspend fun read(id: String): String {
        return transaction {
            val session = UserSessionEntity.find { UsersSessionsTable.sessionId eq id }.firstOrNull()


            if (session == null) throw NoSuchElementException()

            if (!session.sessionOwner.isActive) {
                clearAllUserSessions(session.sessionOwner.id.value) // na wszelki wypadek, raczej powinny się wyczyścić same
                throw NoSuchElementException()
            }

            session.sessionValue
        }
    }

    fun clearAllUserSessions(userId: UUID) {
        transaction {
            UserSessionEntity.find { UsersSessionsTable.sessionOwner eq userId }.forEach { it.delete() }
        }
    }
}