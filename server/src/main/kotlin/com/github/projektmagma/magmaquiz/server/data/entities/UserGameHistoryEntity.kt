package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.UsersGameHistoryTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class UserGameHistoryEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, UsersGameHistoryTable),
    DomainCapable<Quiz, QuizConversionCommand.WithUserNoQuestions> {
    companion object : UUIDEntityClass<UserGameHistoryEntity>(UsersGameHistoryTable)

    var quiz by QuizEntity referencedOn UsersGameHistoryTable.quiz
    var user by UserEntity referencedOn UsersGameHistoryTable.user

    override fun toDomain(command: QuizConversionCommand.WithUserNoQuestions): Quiz {
        return transaction {
            quiz.toDomain(command).apply {
                lastPlayedAt = super.createdAt.epochSecond
            }
        }

    }
}