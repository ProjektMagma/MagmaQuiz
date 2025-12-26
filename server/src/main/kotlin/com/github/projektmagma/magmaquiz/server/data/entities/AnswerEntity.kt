package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.Answer
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
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