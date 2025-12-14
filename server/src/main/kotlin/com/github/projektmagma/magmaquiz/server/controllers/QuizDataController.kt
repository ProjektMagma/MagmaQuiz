package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.data.domain.Quiz
import com.github.projektmagma.magmaquiz.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.data.rest.values.CreateOrModifyQuizValue
import com.github.projektmagma.magmaquiz.server.data.entities.AnswerEntity
import com.github.projektmagma.magmaquiz.server.data.entities.QuestionEntity
import com.github.projektmagma.magmaquiz.server.data.entities.QuizEntity
import com.github.projektmagma.magmaquiz.server.data.entities.UserEntity
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.http.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction

class QuizDataController {

    fun tryCreateQuiz(
        createOrModifyQuizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val postQuiz = createOrModifyQuizValue.quiz

        val isNameAlreadyTaken = transaction {
            !QuizEntity.find { QuizzesTable.quizName eq postQuiz.quizName }.empty()
        }

        if (isNameAlreadyTaken)
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            val innerQuiz = QuizEntity.new {
                quizCreator = dbUser
                quizName = postQuiz.quizName
                quizDescription = postQuiz.quizDescription
                quizImage = postQuiz.quizImage
                isPublic = postQuiz.isPublic
            }
            postQuiz.questionList?.forEach { q ->
                val innerQuestion = QuestionEntity.new {
                    quiz = innerQuiz
                    questionNumber = q.questionNumber
                    questionContent = q.questionContent
                    questionImage = q.questionImage
                }
                q.answerList?.forEach { a ->
                    AnswerEntity.new {
                        question = innerQuestion
                        answerContent = a.answerContent
                        isCorrect = a.isCorrect
                    }
                }
            }
        }
        return NetworkResource.Success(Unit)

    }

    fun findQuizzesByName(stringToSearch: String): NetworkResource<List<Quiz>> {
        val quizzesList = transaction {

            val query = QuizzesTable.select(QuizzesTable.columns)
                .where {
                    QuizzesTable.quizName.lowerCase() like "%${stringToSearch.lowercase()}%" and QuizzesTable.isActive and QuizzesTable.isPublic
                }
            QuizEntity.wrapRows(query).map { it.toDomain() }
        }

        return NetworkResource.Success(
            quizzesList
        )
    }

    fun tryGetQuizData(quizId: Int?, session: UserSession): NetworkResource<Quiz> {

        if (quizId == null)
            return NetworkResource.Error(HttpStatusCode.BadRequest)

        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val dbQuiz = transaction {
            QuizEntity.findById(quizId)
        }

        if (dbQuiz == null || !dbQuiz.isActive)
            return NetworkResource.Error(HttpStatusCode.NotFound)

        if (!dbQuiz.isPublic && dbQuiz.quizCreator != dbUser)
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        return NetworkResource.Success(dbQuiz.toDomainWithChildren())
    }
}