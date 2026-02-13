package com.github.projektmagma.magmaquiz.server.repository

import com.github.projektmagma.magmaquiz.server.data.entities.*
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
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
                isPublic = quizValue.isPublic
            }
            quizValue.questionList.forEach { q ->
                val innerQuestion = QuestionEntity.new {
                    quiz = innerQuiz
                    questionNumber = q.questionNumber
                    questionContent = q.questionContent
                    questionImage = q.questionImage
                }
                q.answerList.forEach { a ->
                    AnswerEntity.new {
                        question = innerQuestion
                        answerContent = a.answerContent
                        isCorrect = a.isCorrect
                    }
                }
            }
            innerQuiz
        }
    }

    fun getQuizData(quizId: UUID): QuizEntity? {
        return transaction { QuizEntity.find { QuizzesTable.id eq quizId and QuizzesTable.isActive }.firstOrNull() }
    }

    fun getQuizzesByName(quizName: String? = null): List<QuizEntity> {
        return transaction {
            if (quizName.isNullOrBlank())
                QuizEntity.find {
                    QuizzesTable.isActive and QuizzesTable.isPublic
                }
                    .toList()
            else
                QuizEntity.find {
                    QuizzesTable.quizName.lowerCase() like "%${quizName.lowercase()}%" and
                            QuizzesTable.isActive and QuizzesTable.isPublic
                }
                    .toList()
        }
    }

    fun changeFavoriteStatus(quizEntity: QuizEntity, userEntity: UserEntity): Boolean {
        return transaction {
            var favoriteStatus =
                FavoriteQuizzesEntity.find {
                    FavoriteQuizzesTable.user eq userEntity.id and (FavoriteQuizzesTable.quiz eq quizEntity.id)
                }
                    .firstOrNull()

            if (favoriteStatus == null) {
                favoriteStatus = FavoriteQuizzesEntity.new {
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
            quizEntity.questionList.forEach { q ->
                q.isActive = false
                q.answerList.forEach { a ->
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
                it.isPublic = newData.isPublic
            }

            val existingQuestions = mutableListOf<EntityID<UUID>>()
            val existingAnswers = mutableListOf<EntityID<UUID>>()

            newData.questionList.forEach { postQuestion ->
                if (postQuestion.id != null) { // modyfikacja starych pytania
                    val q = transaction {
                        QuestionEntity.findByIdAndUpdate(postQuestion.id!!) {
                            it.questionNumber = postQuestion.questionNumber
                            it.questionContent = postQuestion.questionContent
                            it.questionImage = postQuestion.questionImage
                        }!!
                    }
                    existingQuestions.add(q.id)
                    postQuestion.answerList.forEach { postAnswer -> // modyfikacja starych odpowiedzi
                        if (postAnswer.id != null) {
                            val a = transaction {
                                AnswerEntity.findByIdAndUpdate(postAnswer.id!!) {
                                    it.answerContent = postAnswer.answerContent
                                    it.isCorrect = postAnswer.isCorrect
                                }!!
                            }
                            existingAnswers.add(a.id)
                        } else {
                            val a = transaction {
                                AnswerEntity.new { // nowe odpowiedzi do starego pytania
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
                        QuestionEntity.new { // nowe pytanie
                            quiz = quizEntity
                            questionNumber = postQuestion.questionNumber
                            questionContent = postQuestion.questionContent
                            questionImage = postQuestion.questionImage
                        }
                    }
                    existingQuestions.add(q.id)

                    postQuestion.answerList.forEach { answer -> // nowe odpowiedzi
                        val a = transaction {
                            AnswerEntity.new {
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
                QuestionEntity.find { // Usuń nieaktywne pytania razem z odpowiedziami
                    QuestionsTable.id notInList existingQuestions and (QuestionsTable.quiz eq QuestionsTable.id)
                }
                    .forEach { q ->
                        q.isActive = false
                        q.answerList.forEach { a -> a.isActive = false }
                    }

                AnswerEntity.find { // Usuń nieaktywne odpowiedzi
                    AnswersTable.id notInList existingAnswers and (AnswersTable.question inList existingAnswers)
                }
                    .forEach { it.isActive = false }
            }
        }
    }
}