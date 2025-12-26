package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.data.domain.Quiz
import com.github.projektmagma.magmaquiz.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.data.rest.values.CreateOrModifyQuizValue
import com.github.projektmagma.magmaquiz.server.data.conversion.HasChildrenConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.*
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class QuizDataController {

    fun quizCreate(
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
        return NetworkResource.Success(Unit, HttpStatusCode.Created)
    }

    // TODO: Sprawdzić czy na pewno działa
    fun quizModify(
        createOrModifyQuizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {

        val postQuiz = createOrModifyQuizValue.quiz

        val modifiedQuiz = transaction { QuizEntity.findById(postQuiz.id!!) }

        if (modifiedQuiz == null)
            return NetworkResource.Error(HttpStatusCode.NotFound)

        if (transaction { modifiedQuiz.quizCreator.id.value } != session.userId)
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        val isNameAlreadyTaken = transaction {
            !QuizEntity.find { QuizzesTable.quizName eq postQuiz.quizName }.empty()
        }

        if (isNameAlreadyTaken && transaction { postQuiz.quizName != modifiedQuiz.quizName })
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {

            QuizEntity.findByIdAndUpdate(modifiedQuiz.id.value) {
                it.quizName = postQuiz.quizName
                it.quizDescription = postQuiz.quizDescription
                it.quizImage = postQuiz.quizImage
                it.isPublic = postQuiz.isPublic
            }

            val existingQuestions = mutableListOf<EntityID<UUID>>()
            val existingAnswers = mutableListOf<EntityID<UUID>>()

            postQuiz.questionList?.forEach { postQuestion ->
                if (postQuestion.id != null) { // modyfikacja starych pytania
                    val q = transaction {
                        QuestionEntity.findByIdAndUpdate(postQuestion.id!!) {
                            it.questionNumber = postQuestion.questionNumber
                            it.questionContent = postQuestion.questionContent
                            it.questionImage = postQuestion.questionImage
                        }!!
                    }
                    existingQuestions.add(q.id)
                    postQuestion.answerList?.forEach { postAnswer -> // modyfikacja starych odpowiedzi
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
                            quiz = modifiedQuiz
                            questionNumber = postQuestion.questionNumber
                            questionContent = postQuestion.questionContent
                            questionImage = postQuestion.questionImage
                        }
                    }
                    existingQuestions.add(q.id)

                    postQuestion.answerList?.forEach { answer -> // nowe odpowiedzi
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
        return NetworkResource.Success(Unit)
    }

    fun quizFindByName(stringToSearch: String, session: UserSession): NetworkResource<List<Quiz>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val quizzesList = transaction {
            QuizEntity.find {
                QuizzesTable.quizName.lowerCase() like "%${stringToSearch.lowercase()}%" and
                        QuizzesTable.isActive and QuizzesTable.isPublic
            }
                .map { dbQuiz ->
                    dbQuiz.toDomain(HasChildrenConversionCommand.NoChildren).apply {
                        val favoriteStatus = transaction {
                            FavoriteQuizzesEntity.find {
                                FavoriteQuizzesTable.user eq dbUser.id and
                                        (FavoriteQuizzesTable.quiz eq dbQuiz.id)
                            }
                        }.firstOrNull()

                        this.likedByYou = favoriteStatus != null && favoriteStatus.isActive
                    }
                }
        }

        return NetworkResource.Success(
            quizzesList,
            HttpStatusCode.PartialContent
        )
    }

    fun quizFromId(quizId: UUID, session: UserSession): NetworkResource<Quiz> {


        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val dbQuiz = transaction {
            QuizEntity.findById(quizId)
        }

        if (dbQuiz == null ||
            transaction {
                !dbQuiz.isActive || !dbQuiz.isPublic && dbQuiz.quizCreator.id != dbUser.id
            }
        )
            return NetworkResource.Error(HttpStatusCode.NotFound)


        return NetworkResource.Success(transaction {
            dbQuiz.toDomain(HasChildrenConversionCommand.WithChildren)
                .apply {
                    val favoriteStatus = transaction {
                        FavoriteQuizzesEntity.find { FavoriteQuizzesTable.user eq dbUser.id and (FavoriteQuizzesTable.quiz eq dbQuiz.id) }
                    }.firstOrNull()

                    this.likedByYou = favoriteStatus != null && favoriteStatus.isActive
                }
        }, HttpStatusCode.Found)
    }

    fun quizChangeFavoriteStatus(quizId: UUID, session: UserSession): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val dbQuiz = transaction {
            QuizEntity.findById(quizId)
        }

        if (dbQuiz == null ||
            transaction {
                !dbQuiz.isActive || !dbQuiz.isPublic && dbQuiz.quizCreator.id != dbUser.id
            }
        )
            return NetworkResource.Error(HttpStatusCode.NotFound)

        val favoriteStatus = transaction {
            FavoriteQuizzesEntity.find { FavoriteQuizzesTable.user eq dbUser.id and (FavoriteQuizzesTable.quiz eq dbQuiz.id) }
                .firstOrNull()
        }

        if (favoriteStatus == null) {
            transaction {
                FavoriteQuizzesEntity.new {
                    user = dbUser
                    quiz = dbQuiz
                }
                dbQuiz.likesCount++
            }
        } else transaction {
            favoriteStatus.isActive = !favoriteStatus.isActive
            if (favoriteStatus.isActive) dbQuiz.likesCount++ else dbQuiz.likesCount--
        }
        return NetworkResource.Success(Unit)
    }

    fun quizMyFavorites(session: UserSession): NetworkResource<List<Quiz>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val quizzesList = transaction {
            val favorites =
                FavoriteQuizzesEntity.find {
                    FavoriteQuizzesTable.user eq dbUser.id and
                            (FavoriteQuizzesTable.isActive)
                }
                    .map { it.quiz.id }

            QuizEntity.find {
                QuizzesTable.isActive eq true and
                        (QuizzesTable.isPublic eq true or (QuizzesTable.quizCreator eq dbUser.id)) and
                        (QuizzesTable.id inList (favorites))
            }.map { dbQuiz ->
                dbQuiz.toDomain(HasChildrenConversionCommand.NoChildren).apply { this.likedByYou = true }
            }
        }

        return NetworkResource.Success(quizzesList)
    }
}