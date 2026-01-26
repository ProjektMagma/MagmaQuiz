package com.github.projektmagma.magmaquiz.server.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable


// To jest tabela, gdzie nie robimy soft delete, bo to tylko sesje użytkownika, więc chyba bez sensu to jest
object UsersSessionsTable : IntIdTable("users_sessions", "id") {
    val sessionId = varchar("session_id", 255)
    val sessionValue = varchar("session_value", 255)
    val sessionOwner = reference("session_owner_id", UsersTable)
}