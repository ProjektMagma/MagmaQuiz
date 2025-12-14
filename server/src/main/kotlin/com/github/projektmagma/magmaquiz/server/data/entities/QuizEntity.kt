package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.Quiz
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainWithChildrenCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtIntEntity
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class QuizEntity(id: EntityID<Int>) : ExtIntEntity(id, QuizzesTable), DomainWithChildrenCapable<Quiz> {
    companion object : IntEntityClass<QuizEntity>(QuizzesTable)

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    val questionList by QuestionEntity referrersOn QuestionsTable.quiz

    override fun toDomain(): Quiz {
        return transaction {
            Quiz(
                id = super.id.value,
                quizName = quizName,
                quizDescription = quizDescription,
                quizImage = quizImage,
                isPublic = isPublic,
                quizCreatorName = quizCreator.userName,
                createdAt = createdAt.epochSecond,
                modifiedAt = modifiedAt.epochSecond,
            )
        }
    }

    override fun toDomainWithChildren(): Quiz {
        return transaction {
            Quiz(
                id = super.id.value,
                quizName = quizName,
                quizDescription = quizDescription,
                quizImage = quizImage,
                isPublic = isPublic,
                quizCreatorName = quizCreator.userName,
                createdAt = createdAt.epochSecond,
                modifiedAt = modifiedAt.epochSecond,
                questionList = questionList.map { it.toDomain() }
            )
        }
    }
}