package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.controllers.util.favoritesQuizzes
import com.github.projektmagma.magmaquiz.server.controllers.util.friendships
import com.github.projektmagma.magmaquiz.server.controllers.util.quizEntityOrNull
import com.github.projektmagma.magmaquiz.server.controllers.util.toUserList
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.*
import com.github.projektmagma.magmaquiz.server.data.tables.AnswersTable
import com.github.projektmagma.magmaquiz.server.data.tables.FavoriteQuizzesTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuestionsTable
import com.github.projektmagma.magmaquiz.server.data.tables.QuizzesTable
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class QuizDataController {

    fun quizCreate(
        quizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val isNameAlreadyTaken = transaction {
            !QuizEntity.find { QuizzesTable.quizName eq quizValue.quizName }.empty()
        }

        if (isNameAlreadyTaken)
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {
            val innerQuiz = QuizEntity.new {
                quizCreator = dbUser
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
        }
        return NetworkResource.Success(Unit, HttpStatusCode.Created)
    }

    // TODO: Sprawdzić czy na pewno działa
    fun quizModify(
        quizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {

        val modifiedQuiz = transaction { QuizEntity.findById(quizValue.id!!) }

        if (modifiedQuiz == null)
            return NetworkResource.Error(HttpStatusCode.NotFound)

        if (transaction { modifiedQuiz.quizCreator.id.value } != session.userId)
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        val isNameAlreadyTaken = transaction {
            !QuizEntity.find { QuizzesTable.quizName eq quizValue.quizName }.empty()
        }

        if (isNameAlreadyTaken && transaction { quizValue.quizName != modifiedQuiz.quizName })
            return NetworkResource.Error(HttpStatusCode.Conflict)

        transaction {

            QuizEntity.findByIdAndUpdate(modifiedQuiz.id.value) {
                it.quizName = quizValue.quizName
                it.quizDescription = quizValue.quizDescription
                it.quizImage = quizValue.quizImage
                it.isPublic = quizValue.isPublic
            }

            val existingQuestions = mutableListOf<EntityID<UUID>>()
            val existingAnswers = mutableListOf<EntityID<UUID>>()

            quizValue.questionList.forEach { postQuestion ->
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
                            quiz = modifiedQuiz
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
        return NetworkResource.Success(Unit)
    }

    fun quizFindByName(session: UserSession, stringToSearch: String? = null): NetworkResource<List<Quiz>> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!

        val quizzesList = transaction {
            if (stringToSearch.isNullOrBlank())
                QuizEntity.find {
                    QuizzesTable.isActive and QuizzesTable.isPublic
                }
                    .sortedBy { it.createdAt }
                    .reversed()
                    .take(100)
            else
                QuizEntity.find {
                    QuizzesTable.quizName.lowerCase() like "%${stringToSearch.lowercase()}%" and
                            QuizzesTable.isActive and QuizzesTable.isPublic
                }
        }

        val quizzesMapped = transaction {
            quizzesList.map { dbQuiz ->
                dbQuiz.toDomain(QuizConversionCommand.WithUserNoQuestions).apply {
                    val favoriteStatus =
                        transaction { FavoriteQuizzesEntity.find { FavoriteQuizzesTable.user eq dbUser.id and (FavoriteQuizzesTable.quiz eq dbQuiz.id) } }.firstOrNull()
                    this.likedByYou =
                        favoriteStatus != null && favoriteStatus.isActive
                }
            }
        }
        return NetworkResource.Success(
            quizzesMapped,
            HttpStatusCode.PartialContent
        )
    }

    fun quizFromId(quizId: UUID, session: UserSession): NetworkResource<Quiz> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!
        val dbQuiz = quizEntityOrNull(quizId, session.userId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        return NetworkResource.Success(transaction {
            dbQuiz.toDomain(QuizConversionCommand.WithUserAndQuestions)
                .apply {
                    val favoriteStatus = transaction {
                        FavoriteQuizzesEntity.find { FavoriteQuizzesTable.user eq dbUser.id and (FavoriteQuizzesTable.quiz eq dbQuiz.id) }
                    }.firstOrNull()

                    this.likedByYou = favoriteStatus != null && favoriteStatus.isActive
                }
        })
    }

    fun quizChangeFavoriteStatus(quizId: UUID, session: UserSession): NetworkResource<Unit> {
        val dbUser = transaction {
            UserEntity.findById(session.userId)
        }!!
        val dbQuiz = quizEntityOrNull(quizId, session.userId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

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
            dbUser.favoritesQuizzes()
                .map { dbQuiz ->
                    dbQuiz.toDomain(QuizConversionCommand.WithUserNoQuestions).apply { this.likedByYou = true }
                }
        }

        return NetworkResource.Success(quizzesList)
    }

    fun quizFindByUserId(
        userId: UUID,
        session: UserSession
    ): NetworkResource<List<Quiz>> = transaction {
        val profileUser = UserEntity.findById(userId)
            ?: return@transaction NetworkResource.Error(HttpStatusCode.NotFound)

        val viewerUser = UserEntity.findById(session.userId)
            ?: return@transaction NetworkResource.Error(HttpStatusCode.Unauthorized)

        val isOwner = profileUser.id.value == viewerUser.id.value

        val quizzes: List<QuizEntity> = profileUser.quizList
            .filter { it.isActive }
            .filter { isOwner || it.isPublic }
            .toList()

        val likedQuizIds =
            FavoriteQuizzesEntity.find {
                (FavoriteQuizzesTable.user eq viewerUser.id) and
                        (FavoriteQuizzesTable.isActive eq true)
            }.map { it.quiz.id }

        val mapped = quizzes.map { dbQuiz ->
            dbQuiz.toDomain(QuizConversionCommand.WithUserNoQuestions).apply {
                likedByYou = dbQuiz.id in likedQuizIds
            }
        }

        NetworkResource.Success(mapped)
    }

    fun quizDelete(quizId: UUID): NetworkResource<Unit> = transaction {
        val dbQuiz = QuizEntity.findById(quizId) ?: return@transaction NetworkResource.Error(HttpStatusCode.NotFound)

        dbQuiz.isActive = false

        QuestionEntity.find {
            (QuestionsTable.quiz eq dbQuiz.id) and (QuestionsTable.isActive eq true)
        }.forEach { q ->
            q.isActive = false
            AnswerEntity.find {
                (AnswersTable.question eq q.id) and (AnswersTable.isActive eq true)
            }.forEach { a -> a.isActive = false }
        }

        FavoriteQuizzesEntity.find {
            (FavoriteQuizzesTable.quiz eq dbQuiz.id) and (FavoriteQuizzesTable.isActive eq true)
        }.forEach { fav -> fav.isActive = false }


        return@transaction NetworkResource.Success(Unit, HttpStatusCode.OK)
    }

    fun quizNewest(session: UserSession, count: Int): NetworkResource<List<Quiz>> {
        val quizList = transaction {
            val userFavorites = transaction { UserEntity.findById(session.userId)!!.favoritesQuizzes() }
            QuizEntity.all()
                .filter { it.isActive && it.isPublic }
                .sortedBy { it.createdAt }
                .asReversed()
                .take(count)
                .map { dbQuiz ->
                    dbQuiz.toDomain(QuizConversionCommand.WithUserNoQuestions)
                        .apply {
                            this.likedByYou = dbQuiz in userFavorites
                        }
                }
        }

        return NetworkResource.Success(quizList)
    }

    fun quizMostLiked(session: UserSession, count: Int): NetworkResource<List<Quiz>> {
        val quizList = transaction {
            val userFavorites = transaction { UserEntity.findById(session.userId)!!.favoritesQuizzes() }
            QuizEntity.all()
                .filter { it.isActive && it.isPublic }
                .sortedBy { it.likesCount }
                .asReversed()
                .take(count)
                .map { quiz ->
                    quiz.toDomain(QuizConversionCommand.WithUserNoQuestions)
                        .apply {
                            this.likedByYou = quiz in userFavorites
                        }
                }
        }

        return NetworkResource.Success(quizList)
    }


    // TODO: Sprawdzić czy działa
    fun quizFriendsQuizzes(session: UserSession): NetworkResource<List<Quiz>> {
        val quizzesList = mutableListOf<Quiz>()
        val dbUser = transaction { UserEntity.findById(session.userId)!! }
        val friendList = transaction { dbUser.friendships(true).toUserList(dbUser) }
        val userFavorites = transaction { dbUser.favoritesQuizzes() }

        transaction {
            friendList.forEach {
                it.quizList.forEach { quiz ->
                    quizzesList.add(
                        quiz.toDomain(QuizConversionCommand.WithUserNoQuestions).apply {
                            likedByYou = quiz in userFavorites
                        })
                }
            }

            quizzesList.apply {
                this.sortBy { it.createdAt }
                this.reverse()
            }
        }

        return NetworkResource.Success(quizzesList)
    }
}