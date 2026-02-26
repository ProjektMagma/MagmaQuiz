package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesQuestionsAnswersTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizQuestionAnswerEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesQuestionsAnswersTable),
    DomainCapable<Answer, ConversionCommand> {
    companion object : UUIDEntityClass<QuizQuestionAnswerEntity>(QuizzesQuestionsAnswersTable)

    var question by QuizQuestionEntity referencedOn QuizzesQuestionsAnswersTable.question
    var answerContent by QuizzesQuestionsAnswersTable.answerContent
    var isCorrect by QuizzesQuestionsAnswersTable.isCorrect
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