package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.data.domain.Quiz
import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainWithChildrenCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class QuizEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTable), DomainWithChildrenCapable<Quiz> {
    companion object : UUIDEntityClass<QuizEntity>(QuizzesTable)

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    var likesCount by QuizzesTable.likesCount
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
                likesCount = likesCount,
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
                likesCount = likesCount,
                createdAt = createdAt.epochSecond,
                modifiedAt = modifiedAt.epochSecond,
                questionList = questionList.map { it.toDomain() }
            )
        }
    }
}