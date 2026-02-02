package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class AnswerEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, AnswersTable), DomainCapable<Answer, ConversionCommand> {
    companion object : UUIDEntityClass<AnswerEntity>(AnswersTable)

    var question by QuestionEntity referencedOn AnswersTable.question
    var answerContent by AnswersTable.answerContent
    var isCorrect by AnswersTable.isCorrect
    override fun toDomain(command: ConversionCommand): Answer {
        return transaction {
            Answer(
                id = super.id.value,
                answerContent = answerContent,
                isCorrect = isCorrect
            )
        }
    }
}