package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesQuestionsAnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesQuestionsTable
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizQuestionEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesQuestionsTable),
    DomainCapable<Question, ConversionCommand> {
    companion object : UUIDEntityClass<QuizQuestionEntity>(QuizzesQuestionsTable)

    var questionNumber by QuizzesQuestionsTable.questionNumber
    var questionContent by QuizzesQuestionsTable.questionContent
    var questionImage by QuizzesQuestionsTable.questionImage
    var quiz by QuizEntity referencedOn QuizzesQuestionsTable.quiz
    private val answerList by QuizQuestionAnswerEntity referrersOn QuizzesQuestionsAnswersTable.question

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

    fun getAnswers(): List<QuizQuestionAnswerEntity> {
        return transaction { answerList.toList() }
    }
}