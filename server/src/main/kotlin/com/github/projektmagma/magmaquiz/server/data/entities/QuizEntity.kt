package com.github.projektmagma.magmaquiz.server.data.entities

import com.github.projektmagma.magmaquiz.server.data.abstraction.DomainCapable
import com.github.projektmagma.magmaquiz.server.data.abstraction.ExtUUIDEntity
import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.UserConversionCommand
import com.github.projektmagma.magmaquiz.server.data.tables.*
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.FriendshipStatus
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
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
                find { QuizzesTable.quizName eq newQuizName and QuizzesTable.isActive }.any()
            }
        }
    }

    var quizName by QuizzesTable.quizName
    var quizDescription by QuizzesTable.quizDescription
    var quizImage by QuizzesTable.quizImage
    var visibility by QuizzesTable.visibility
    var likesCount by QuizzesTable.likesCount
    var quizCreator by UserEntity referencedOn QuizzesTable.quizCreator
    private val questionList by QuizQuestionEntity referrersOn QuizzesQuestionsTable.quiz
    private val reviewList by QuizReviewEntity referrersOn QuizzesReviewsTable.quiz
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
                        visibility = visibilityToDomain(),
                        quizCreator = quizCreator,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = questionList.map { it.toDomain(ConversionCommand.Default) },
                        likedByYou = isFavoriteByUser(command.caller),
                        reviewCount = reviewList.count { it.isActive },
                        reviewedByYou = isReviewedByUser(command.caller),
                        averageRating = getAverageRating(),
                        tagList = tagList.map { it.toDomain(ConversionCommand.Default) },
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
                        visibility = visibilityToDomain(),
                        quizCreator = quizCreator,
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = emptyList(),
                        likedByYou = isFavoriteByUser(command.caller),
                        reviewCount = reviewList.count { it.isActive },
                        reviewedByYou = isReviewedByUser(command.caller),
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
                        visibility = visibilityToDomain(),
                        likesCount = likesCount,
                        createdAt = createdAt.epochSecond,
                        modifiedAt = modifiedAt.epochSecond,
                        questionList = emptyList(),
                        likedByYou = isFavoriteByUser(command.caller),
                        reviewCount = reviewList.count { it.isActive },
                        reviewedByYou = isReviewedByUser(command.caller),
                        averageRating = getAverageRating(),
                        tagList = tagList.map { it.toDomain(ConversionCommand.Default) }
                    )
            }
        }
    }

    private fun visibilityToDomain(): QuizVisibility {
        return transaction { QuizVisibility.entries.first { it.numberInDatabase == visibility } }
    }

    private fun getAverageRating(): Float {
        return transaction {
            val ratings = reviewList.map { it.rating }
            if (ratings.isEmpty()) 0f else ratings.average().toFloat()
        }
    }

    private fun isFavoriteByUser(user: UserEntity): Boolean {
        return transaction { UserFavoriteQuizzesEntity.isFavoriteByUser(this@QuizEntity, user) }
    }

    private fun isReviewedByUser(user: UserEntity): Boolean {
        return transaction { QuizReviewEntity.isReviewedByUser(this@QuizEntity, user) }
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

    fun isAccessibleByUser(thisUser: UserEntity): Boolean {
        val visibilityStatus = visibilityToDomain()
        return transaction {

            when (visibilityStatus) {
                QuizVisibility.Private -> isUserCreator(thisUser)
                QuizVisibility.FriendsOnly -> thisUser.checkFriendship(this@QuizEntity.quizCreator) == FriendshipStatus.Friends
                QuizVisibility.Public -> true
            }
        }
    }
}
