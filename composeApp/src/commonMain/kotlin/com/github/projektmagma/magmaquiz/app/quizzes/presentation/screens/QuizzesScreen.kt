package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizSearchFieldAndFilters
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizListCommand
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun QuizzesScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val state by quizzesListViewModel.quizListState.collectAsStateWithLifecycle()
    val uiState by quizzesListViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.quizFilter) {
        if (!state.isLoaded) quizzesListViewModel.onCommand(QuizListCommand.LoadByFilter)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AutoScalableLazyColumn(
            itemList = state.quizzes,
            key = { it.id!! },
            uiState = uiState,
            stickyHeader = {
                QuizSearchFieldAndFilters(it)
            },
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
                    }
                )
            }
        )
    }
}

