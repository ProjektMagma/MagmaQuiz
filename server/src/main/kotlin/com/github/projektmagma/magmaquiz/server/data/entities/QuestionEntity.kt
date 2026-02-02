package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuestionEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuestionsTable),
    DomainCapable<Question, ConversionCommand> {
    companion object : UUIDEntityClass<QuestionEntity>(QuestionsTable)

    var quiz by QuizEntity referencedOn QuestionsTable.quiz
    var questionNumber by QuestionsTable.questionNumber
    var questionContent by QuestionsTable.questionContent
    var questionImage by QuestionsTable.questionImage
    val answerList by AnswerEntity referrersOn AnswersTable.question

    override fun toDomain(command: ConversionCommand): Question {
        return transaction {
            Question(
                id = super.id.value,
                questionNumber = questionNumber,
                questionContent = questionContent,
                questionImage = questionImage,
                answerList = answerList.map { it.toDomain(ConversionCommand.Default) }
            )
        }
    }
}