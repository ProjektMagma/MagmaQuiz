package com.github.projektmagma.magmaquiz.server.controllers

import com.github.projektmagma.magmaquiz.server.data.conversion.ConversionCommand
import com.github.projektmagma.magmaquiz.server.data.conversion.QuizConversionCommand
import com.github.projektmagma.magmaquiz.server.data.entities.QuizEntity
import com.github.projektmagma.magmaquiz.server.data.util.UserSession
import com.github.projektmagma.magmaquiz.server.repository.FriendshipRepository
import com.github.projektmagma.magmaquiz.server.repository.QuizRepository
import com.github.projektmagma.magmaquiz.server.repository.UserRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.domain.Tag
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import io.ktor.http.HttpStatusCode
import java.util.UUID


class QuizDataController(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) {

    fun quizCreate(
        quizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)

        if (QuizEntity.isQuizNameTaken(quizValue.quizName))
            return NetworkResource.Error(HttpStatusCode.Conflict)

        quizRepository.createQuiz(quizValue, thisUser)

        return NetworkResource.Success(Unit, HttpStatusCode.Created)
    }

    // TODO: Sprawdzić czy na pewno działa
    fun quizModify(
        quizValue: CreateOrModifyQuizValue,
        session: UserSession
    ): NetworkResource<Unit> {

        val thisUser = userRepository.getUserData(session)

        val modifiedQuiz =
            quizRepository.getQuizData(quizValue.id!!) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        if (!modifiedQuiz.isUserCreator(thisUser))
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        if (QuizEntity.isQuizNameTaken(quizValue.quizName) && quizValue.quizName != modifiedQuiz.quizName)
            return NetworkResource.Error(HttpStatusCode.Conflict)

        quizRepository.modifyQuiz(modifiedQuiz, quizValue)

        return NetworkResource.Success(Unit)
    }

    fun quizFindByName(session: UserSession, count: Int, stringToSearch: String?): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)

        val quizzesList = quizRepository.getQuizzesByName(stringToSearch)
            .sortedBy { it.createdAt }
            .reversed()
            .take(count)
            .map {
                it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser))
            }

        return NetworkResource.Success(
            quizzesList,
            HttpStatusCode.PartialContent
        )
    }

    fun quizFromId(quizId: UUID, session: UserSession): NetworkResource<Quiz> {
        val thisUser = userRepository.getUserData(session)

        val dbQuiz = quizRepository.getQuizData(quizId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        if (!dbQuiz.isPublic && !dbQuiz.isUserCreator(thisUser))
            return NetworkResource.Error(HttpStatusCode.Forbidden)


        return NetworkResource.Success(
            dbQuiz.toDomain(QuizConversionCommand.WithUserAndQuestions(thisUser))
        )
    }

    fun quizChangeFavoriteStatus(quizId: UUID, session: UserSession): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)
        val dbQuiz = quizRepository.getQuizData(quizId) ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        if (!dbQuiz.isPublic && !dbQuiz.isUserCreator(thisUser))
            return NetworkResource.Error(HttpStatusCode.Forbidden)


        quizRepository.changeFavoriteStatus(dbQuiz, thisUser)
        return NetworkResource.Success(Unit)
    }

    fun quizMyFavorites(session: UserSession, count: Int, stringToSearch: String?): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)

        val quizzesList =
            thisUser.favoriteQuizzes()
                .filter { stringToSearch.isNullOrBlank() || it.quizName.equals(stringToSearch, true) }
                .take(count)
                .map { it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser)) }

        return NetworkResource.Success(quizzesList)
    }

    fun quizFindByUserId(session: UserSession, userId: UUID): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)

        val profileUser = userRepository.getUserData(userId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)


        val quizList =
            profileUser.getUserQuizzes().filter { it.isUserCreator(thisUser) || it.isPublic }.map {
                it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser))
            }

        return NetworkResource.Success(quizList)
    }

    fun quizDelete(session: UserSession, quizId: UUID): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)
        val dbQuiz = quizRepository.getQuizData(quizId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        if (!dbQuiz.isUserCreator(thisUser))
            return NetworkResource.Error(HttpStatusCode.Forbidden)

        quizRepository.deleteQuiz(dbQuiz)

        return NetworkResource.Success(Unit, HttpStatusCode.OK)
    }

    fun quizNewest(session: UserSession, count: Int, stringToSearch: String?): NetworkResource<List<Quiz>> {

        val thisUser = userRepository.getUserData(session)

        val quizList = quizRepository.getQuizzesByName()
            .filter { stringToSearch.isNullOrBlank() || it.quizName.equals(stringToSearch, true) }
            .sortedBy { it.createdAt }
            .reversed()
            .take(count)
            .map { it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser)) }

        return NetworkResource.Success(quizList, HttpStatusCode.PartialContent)
    }

    fun quizMostLiked(session: UserSession, count: Int, stringToSearch: String?): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)

        val quizList = quizRepository.getQuizzesByName()
            .filter { stringToSearch.isNullOrBlank() || it.quizName.equals(stringToSearch, true) }
            .sortedBy { it.likesCount }
            .reversed()
            .take(count)
            .map { it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser)) }


        return NetworkResource.Success(quizList, HttpStatusCode.PartialContent)
    }


    fun quizFriendsQuizzes(session: UserSession, count: Int, stringToSearch: String?): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)
        val quizzesList = mutableListOf<QuizEntity>()
        friendshipRepository.userFriendList(thisUser).forEach { quizzesList.addAll(it.getUserQuizzes()) }


        return NetworkResource.Success(
            quizzesList
                .filter { stringToSearch.isNullOrBlank() || it.quizName.equals(stringToSearch, true) }
                .sortedBy { it.likesCount }
                .take(count)
                .map {
                    it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser))
                }, HttpStatusCode.PartialContent
        )
    }

    fun quizMarkAsPlayed(session: UserSession, quizId: UUID): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)
        val dbQuiz = quizRepository.getQuizData(quizId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        quizRepository.markAsPlayed(dbQuiz, thisUser)

        return NetworkResource.Success(Unit)
    }

    fun quizMyGameHistory(session: UserSession): NetworkResource<List<Quiz>> {
        val thisUser = userRepository.getUserData(session)

        return NetworkResource.Success(thisUser.getLastPlayedQuizzes().map {
            it.toDomain(QuizConversionCommand.WithUserNoQuestions(thisUser))
        })
    }

    fun quizCreateReview(session: UserSession, quizId: UUID, review: QuizReview): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)
        val dbQuiz = quizRepository.getQuizData(quizId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        if (userRepository.getUserReviews(thisUser).any { it.quiz.id == dbQuiz.id })
            return NetworkResource.Error(HttpStatusCode.Conflict)

        quizRepository.createReview(review, dbQuiz, thisUser)

        return NetworkResource.Success(Unit)
    }

    fun quizReviews(quizId: UUID): NetworkResource<List<QuizReview>> {
        val dbQuiz = quizRepository.getQuizData(quizId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)
        return NetworkResource.Success(
            quizRepository.getQuizReviews(dbQuiz).map { it.toDomain(ConversionCommand.Default) })
    }

    fun quizGetPossibleTags(count: Int, stringToSearch: String?): NetworkResource<List<Tag>> {
        return NetworkResource.Success(
            quizRepository.getExistingTags(count, stringToSearch).map { it.toDomain(ConversionCommand.Default) },
            HttpStatusCode.PartialContent
        )
    }

    fun quizDeleteReview(session: UserSession, quizId: UUID): NetworkResource<Unit> {
        val thisUser = userRepository.getUserData(session)
        val dbQuiz = quizRepository.getQuizData(quizId)
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        val dbReview = userRepository.getUserReviews(thisUser).firstOrNull { it.quiz.id == dbQuiz.id }
            ?: return NetworkResource.Error(HttpStatusCode.NotFound)

        dbReview.setIsActive(false)

        return NetworkResource.Success(Unit)
    }
}