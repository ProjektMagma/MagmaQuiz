package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesQuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesReviewsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTagsMapTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTagsTable
import com.github.projektmagma.magmaquiz.server.data.tables.UsersFavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

class QuizEntity(id: EntityID<UUID>) : ExtUUIDEntity(id, QuizzesTable),
    DomainCapable<Quiz, QuizConversionCommand> {
    companion object : UUIDEntityClass<QuizEntity>(QuizzesTable) {

        fun isQuizNameTaken(newQuizName: String): Boolean {
            return transaction {
                find { QuizzesTable.quizName eq newQuizName and QuizzesTable.isActive }.firstOrNull() != null
            }
        }
    }

    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var isPublic by QuizzesTable.isPublic
    var likesCount by QuizzesTable.likesCount
    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    private val questionList by QuizQuestionEntity referrersOn QuizzesQuestionsTable.quiz
    private val reviewList by QuizReviewEntity referrersOn QuizzesReviewsTable.quiz
    private val favoritesList by UserFavoriteQuizzesEntity referrersOn UsersFavoriteQuizzesTable.quiz
    private val tagList by QuizTagEntity via QuizzesTagsMapTable

    override fun toDomain(command: QuizConversionCommand): Quiz {
        return transaction {
            when (command) {
                is QuizConversionCommand.WithUserAndQuestions -> {
                    val quizCreator =
                        quizCreator.toDomain(UserConversionCommand.ForeignUser(command.caller))
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
                        likedByYou = isFavoriteByUser(command.caller),
                        averageRating = getAverageRating(),
                        tagList = tagList.map { it.toDomain(ConversionCommand.Default) }
                    )
                }

                is QuizConversionCommand.WithUserNoQuestions -> {
                    val quizCreator =
                        quizCreator.toDomain(UserConversionCommand.ForeignUser(command.caller))
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
                        likedByYou = isFavoriteByUser(command.caller),
                        averageRating = getAverageRating(),
                        tagList = tagList.map { it.toDomain(ConversionCommand.Default) }
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
                        likedByYou = isFavoriteByUser(command.caller),
                        averageRating = getAverageRating(),
                        tagList = tagList.map { it.toDomain(ConversionCommand.Default) }
                    )
            }
        }
    }

    private fun getAverageRating(): Double {
        return transaction {
            val ratings = reviewList.map { it.rating }
            if (ratings.isEmpty()) 0.0 else ratings.average()
        }
    }

    private fun isFavoriteByUser(user: UserEntity): Boolean {
        return transaction { favoritesList.any { it.user.id == user.id && it.isActive } }
    }

    fun getQuestions(): List<QuizQuestionEntity> {
        return transaction { questionList.toList() }
    }

    fun isUserCreator(userEntity: UserEntity): Boolean {
        return transaction { userEntity.id == quizCreator.id }
    }

    fun getTags(): List<QuizTagEntity> {
        return transaction { tagList.toList() }
    }

    fun addTags(tagsStr: List<String>) {
        transaction {
            tagsStr.forEach { tagStr ->
                val tagEntity = QuizTagEntity.find {
                    QuizzesTagsTable.tagName eq tagStr
                }.firstOrNull()

                if (tagEntity != null) {
                    QuizTagMapEntity.new {
                        tag = tagEntity
                        quiz = this@QuizEntity
                    }
                } else {
                    val tagEntity = QuizTagEntity.new {
                        tagName = tagStr
                    }
                    QuizTagMapEntity.new {
                        tag = tagEntity
                        quiz = this@QuizEntity
                    }
                }
            }
        }
    }
}
