package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.*
import com.github.projektmagma.magmaquiz.server.data.tables.*
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class QuizRepository {

    fun createQuiz(quizValue: CreateOrModifyQuizValue, user: UserEntity): QuizEntity {
        return transaction {
            val innerQuiz = QuizEntity.new {
                quizCreator = user
                quizName = quizValue.quizName
                quizDescription = quizValue.quizDescription
                quizImage = quizValue.quizImage
                visibility = quizValue.visibility.numberInDatabase
            }
            quizValue.questionList.forEach { q ->
                val innerQuestion = QuizQuestionEntity.new {
                    quiz = innerQuiz
                    questionNumber = q.questionNumber
                    questionContent = q.questionContent
                    questionImage = q.questionImage
                }
                q.answerList.forEach { a ->
                    QuizQuestionAnswerEntity.new {
                        question = innerQuestion
                        answerContent = a.answerContent
                        isCorrect = a.isCorrect
                    }
                }
            }

            innerQuiz.addTags(quizValue.tagList)


            innerQuiz
        }
    }

    fun getQuizData(quizId: UUID): QuizEntity? {
        return transaction { QuizEntity.find { QuizzesTable.id eq quizId and QuizzesTable.isActive }.firstOrNull() }
    }

    fun getQuizzes(caller: UserEntity, count: Int, offset: Int, stringToSearch: String): List<QuizEntity> {
        return transaction {
            QuizEntity.find {
                QuizzesTable.isActive eq true and (QuizzesTable.quizName.lowerCase() like "%${stringToSearch.lowercase()}%")
            }
                .offset(offset.toLong())
                .limit(count)
                .filter { it.isAccessibleByUser(caller) }
        }
    }

    fun getUserFavoriteQuizzes(
        caller: UserEntity,
        count: Int,
        offset: Int,
        stringToSearch: String
    ): List<QuizEntity> {
        return transaction {
            val userFavoritesIds = UserFavoriteQuizzesEntity.find {
                UsersFavoriteQuizzesTable.isActive eq true and
                        UsersFavoriteQuizzesTable.user.eq(caller.id)
            }.map { it.quiz.id }

            QuizEntity.find {
                QuizzesTable.isActive eq true and (QuizzesTable.id inList (userFavoritesIds)) and (QuizzesTable.quizName.lowerCase() like
                        "%${stringToSearch.lowercase()}%")
            }
                .offset(offset.toLong())
                .limit(count)
                .filter { it.isAccessibleByUser(caller) }
        }
    }

    fun getUserFriendsQuizzes(
        caller: UserEntity,
        callerFriends: List<UserEntity>,
        count: Int,
        offset: Int,
        stringToSearch: String
    ): List<QuizEntity> {
        return transaction {
            QuizEntity.find {
                QuizzesTable.isActive eq true and
                        (QuizzesTable.quizName.lowerCase() like "%${stringToSearch.lowercase()}%") and
                        (QuizzesTable.quizCreator inList (callerFriends.map { it.id }))
            }
                .offset(offset.toLong())
                .limit(count)
                .filter { it.isAccessibleByUser(caller) }
        }
    }

    fun changeFavoriteStatus(quizEntity: QuizEntity, userEntity: UserEntity): Boolean {
        return transaction {
            var favoriteStatus =
                UserFavoriteQuizzesEntity.find {
                    UsersFavoriteQuizzesTable.user eq userEntity.id and (UsersFavoriteQuizzesTable.quiz eq quizEntity.id)
                }
                    .firstOrNull()

            if (favoriteStatus == null) {
                favoriteStatus = UserFavoriteQuizzesEntity.new {
                    user = userEntity
                    quiz = quizEntity
                }
                quizEntity.likesCount++
            } else {
                favoriteStatus.isActive = !favoriteStatus.isActive
                if (favoriteStatus.isActive) quizEntity.likesCount++ else quizEntity.likesCount--
            }
            favoriteStatus.isActive
        }
    }

    fun deleteQuiz(quizEntity: QuizEntity) {
        transaction {
            quizEntity.isActive = false
            quizEntity.getQuestions().forEach { q ->
                q.isActive = false
                q.getAnswers().forEach { a ->
                    a.isActive = false
                }
            }
        }
    }

    fun modifyQuiz(quizEntity: QuizEntity, newData: CreateOrModifyQuizValue) {
        transaction {

            quizEntity.let {
                it.quizName = newData.quizName
                it.quizDescription = newData.quizDescription
                it.quizImage = newData.quizImage
                it.visibility = newData.visibility.numberInDatabase
            }

            val existingQuestions = mutableListOf<EntityID<UUID>>()
            val existingAnswers = mutableListOf<EntityID<UUID>>()

            val tagsToDelete = quizEntity.getTags().filterNot { newData.tagList.contains(it.tagName) }

            tagsToDelete.forEach {
                QuizTagMapEntity.find { QuizzesTagsMapTable.quiz eq quizEntity.id and (QuizzesTagsMapTable.tag eq it.id) }
                    .firstOrNull()?.delete()
            }

            quizEntity.addTags(newData.tagList.filterNot { tagsToDelete.map { t -> t.tagName }.contains(it) })


            newData.questionList.forEach { postQuestion ->
                if (postQuestion.id != null) { // modyfikacja starych pytania
                    val q = transaction {
                        QuizQuestionEntity.findByIdAndUpdate(postQuestion.id!!) {
                            it.questionNumber = postQuestion.questionNumber
                            it.questionContent = postQuestion.questionContent
                            it.questionImage = postQuestion.questionImage
                        }!!
                    }
                    existingQuestions.add(q.id)
                    postQuestion.answerList.forEach { postAnswer -> // modyfikacja starych odpowiedzi
                        if (postAnswer.id != null) {
                            val a = transaction {
                                QuizQuestionAnswerEntity.findByIdAndUpdate(postAnswer.id!!) {
                                    it.answerContent = postAnswer.answerContent
                                    it.isCorrect = postAnswer.isCorrect
                                }!!
                            }
                            existingAnswers.add(a.id)
                        } else {
                            val a = transaction {
                                QuizQuestionAnswerEntity.new { // nowe odpowiedzi do starego pytania
                                    question = q
                                    answerContent = postAnswer.answerContent
                                    isCorrect = postAnswer.isCorrect
                                }
                            }
                            existingAnswers.add(a.id)
                        }
                    }
                } else {
                    val q = transaction {
                        QuizQuestionEntity.new { // nowe pytanie
                            quiz = quizEntity
                            questionNumber = postQuestion.questionNumber
                            questionContent = postQuestion.questionContent
                            questionImage = postQuestion.questionImage
                        }
                    }
                    existingQuestions.add(q.id)

                    postQuestion.answerList.forEach { answer -> // nowe odpowiedzi
                        val a = transaction {
                            QuizQuestionAnswerEntity.new {
                                question = q
                                answerContent = answer.answerContent
                                isCorrect = answer.isCorrect
                            }
                        }
                        existingAnswers.add(a.id)
                    }
                }
            }

            transaction {
                QuizQuestionEntity.find { // Usuń nieaktywne pytania razem z odpowiedziami
                    QuizzesQuestionsTable.id notInList existingQuestions and (QuizzesQuestionsTable.quiz eq QuizzesQuestionsTable.id)
                }
                    .forEach { q ->
                        q.isActive = false
                        q.getAnswers().forEach { a -> a.isActive = false }
                    }

                QuizQuestionAnswerEntity.find { // Usuń nieaktywne odpowiedzi
                    QuizzesQuestionsAnswersTable.id notInList existingAnswers and (QuizzesQuestionsAnswersTable.question inList existingAnswers)
                }
                    .forEach { it.isActive = false }
            }
        }
    }

    fun markAsPlayed(quizEntity: QuizEntity, userEntity: UserEntity) {
        transaction {
            UserGameHistoryEntity.new {
                quiz = quizEntity
                user = userEntity
            }
        }
    }

    fun getQuizReviews(quizEntity: QuizEntity): List<QuizReviewEntity> {
        return transaction {
            QuizReviewEntity.find {
                QuizzesReviewsTable.quiz eq quizEntity.id and (QuizzesReviewsTable.isActive eq true)
            }.toList()
        }
    }

    fun getUserQuizReview(quizEntity: QuizEntity, userEntity: UserEntity): QuizReviewEntity? {
        return transaction {
            QuizReviewEntity.find {
                QuizzesReviewsTable.quiz eq quizEntity.id and (QuizzesReviewsTable.author eq userEntity.id) and (QuizzesReviewsTable.isActive eq true)
            }.firstOrNull()
        }
    }

    fun createReview(review: QuizReview, quizEntity: QuizEntity, userEntity: UserEntity) {
        transaction {
            QuizReviewEntity.new {
                author = userEntity
                quiz = quizEntity
                rating = review.rating
                comment = review.comment
            }
        }
    }

    fun getExistingTags(count: Int, stringToSearch: String): List<QuizTagEntity> {
        return transaction {
            QuizTagEntity.find { QuizzesTagsTable.tagName.lowerCase() like "%${stringToSearch.lowercase()}%" }
                .limit(count)
                .sortedByDescending { it.getQuizzesListCount() }
        }
    }
}