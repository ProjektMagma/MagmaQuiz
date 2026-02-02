package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTable),
    DomainCapable<Quiz, QuizConversionCommand> {
    companion object : UUIDEntityClass<QuizEntity>(QuizzesTable)

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    var likesCount by QuizzesTable.likesCount
    val questionList by QuestionEntity referrersOn QuestionsTable.quiz

    override fun toDomain(command: QuizConversionCommand): Quiz {
        return transaction {
            val quizCreator = quizCreator.toDomain(UserConversionCommand.ForeignUserWithSmallPicture)
                    as ForeignUser

            when (command) {
                QuizConversionCommand.WithUserAndQuestions ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        quizCreator = quizCreator,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = questionList.map { it.toDomain(ConversionCommand.Default) }
                    )

                QuizConversionCommand.WithUserNoQuestions ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        quizCreator = quizCreator,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = emptyList()
                    )

                QuizConversionCommand.WithoutUserAndQuestions ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = emptyList()
                    )
            }
        }
    }
}
