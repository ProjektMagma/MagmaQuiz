package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTable),
    DomainCapable<Quiz, QuizConversionCommand> {
    companion object : UUIDEntityClass<QuizEntity>(QuizzesTable) {

        fun isQuizNameTaken(newQuizName: String): Boolean {
            return transaction {
                find { QuizzesTable.quizName eq newQuizName and QuizzesTable.isActive }.firstOrNull() != null
            }
        }
    }

    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    var likesCount by QuizzesTable.likesCount
    val questionList by QuestionEntity referrersOn QuestionsTable.quiz

    override fun toDomain(command: QuizConversionCommand): Quiz {
        return transaction {
            when (command) {
                is QuizConversionCommand.WithUserAndQuestions -> {
                    val quizCreator =
                        quizCreator.toDomain(UserConversionCommand.ForeignUserWithSmallPicture(command.caller))
                                as ForeignUser
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
                        questionList = questionList.map { it.toDomain(ConversionCommand.Default) },
                        likedByYou = isFavoriteByUser(command.caller)
                    )
                }

                is QuizConversionCommand.WithUserNoQuestions -> {
                    val quizCreator =
                        quizCreator.toDomain(UserConversionCommand.ForeignUserWithSmallPicture(command.caller))
                                as ForeignUser
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
                        questionList = emptyList(),
                        likedByYou = isFavoriteByUser(command.caller)
                    )
                }

                is QuizConversionCommand.WithoutUserAndQuestions ->
                    Quiz(
                        id = super.id.value,
                        quizName = quizName,
                        quizDescription = quizDescription,
                        quizImage = quizImage,
                        isPublic = isPublic,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = emptyList(),
                        likedByYou = isFavoriteByUser(command.caller)
                    )
            }
        }
    }

    private fun isFavoriteByUser(user: UserEntity): Boolean {
        return transaction {
            FavoriteQuizzesEntity.find { FavoriteQuizzesTable.quiz eq this@QuizEntity.id and (FavoriteQuizzesTable.user eq user.id) and (FavoriteQuizzesTable.isActive) }
                .firstOrNull() != null
        }
    }

    fun isUserCreator(userEntity: UserEntity): Boolean {
        return transaction { userEntity.id == quizCreator.id }
    }
}
