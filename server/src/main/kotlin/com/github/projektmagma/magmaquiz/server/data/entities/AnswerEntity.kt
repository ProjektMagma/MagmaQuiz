package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.Answer
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtIntEntity
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AnswerEntity(id: EntityID<Int>) : ExtIntEntity(id, AnswersTable), DomainCapable<Answer> {
    companion object : IntEntityClass<AnswerEntity>(AnswersTable)

    var question by QuestionEntity referencedOn AnswersTable.question
    var answerContent by AnswersTable.answerContent
    var isCorrect by AnswersTable.isCorrect
    override fun toDomain(): Answer {
        return transaction {
            Answer(
                answerContent = answerContent,
                isCorrect = isCorrect
            )
        }
    }
}