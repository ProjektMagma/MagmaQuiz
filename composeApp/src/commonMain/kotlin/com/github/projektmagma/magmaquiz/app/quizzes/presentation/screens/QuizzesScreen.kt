package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizSearchFieldAndFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.skeleton.QuizCardSkeleton
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.list.QuizListCommand
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun QuizzesScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    navigateToQuizReviews: (id: UUID, reviewed: Boolean) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val state by quizzesListViewModel.quizListState.collectAsStateWithLifecycle()
    val uiState by quizzesListViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AutoScalableLazyColumn(
            itemList = state.quizzes,
            key = { it.id!! },
            uiState = uiState,
            isLoadingMore = state.isLoadingMore,
            onLoadMore = { quizzesListViewModel.onCommand(QuizListCommand.LoadMore) },
            stickyHeader = {
                QuizSearchFieldAndFilters(it)
            },
            skeletonContent = { QuizCardSkeleton() },
            content = { quiz ->
                QuizCard(
                    quiz = quiz,
                    navigateToQuizDetails = {
                        navigateToQuizDetails(quiz.id!!)
                    },
                    navigateToUserDetails = {
                        navigateToUserDetails(quiz.quizCreator?.userId!!)
                    },
                    changeFavoriteStatus = {
                        quizzesListViewModel.onCommand(QuizListCommand.FavoriteStatusChanged(quiz.id!!))
                    },
                    navigateToQuizReviews = { navigateToQuizReviews(quiz.id!!, quiz.reviewedByYou) },
                )
            }
        )
    }
}