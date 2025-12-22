package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.Question
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class QuestionEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuestionsTable), DomainCapable<Question> {
    companion object : UUIDEntityClass<QuestionEntity>(QuestionsTable)

    var quiz by QuizEntity referencedOn QuestionsTable.quiz
    var questionNumber by QuestionsTable.questionNumber
    var questionContent by QuestionsTable.questionContent
    var questionImage by QuestionsTable.questionImage
    val answerList by AnswerEntity referrersOn AnswersTable.question

    override fun toDomain(): Question {
        return transaction {
            Question(
                id = super.id.value,
                questionNumber = questionNumber,
                questionContent = questionContent,
                questionImage = questionImage,
                answerList = answerList.map { it.toDomain() }
            )
        }
    }
}