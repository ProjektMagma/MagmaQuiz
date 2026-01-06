package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.FavoritesQuizzesViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ContentLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCard
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun FavoritesQuizzesScreen(
    navigateToQuizDetails: (id: UUID) -> Unit,
    favoritesQuizzesViewModel: FavoritesQuizzesViewModel = koinViewModel()
) {
    val quizzes by favoritesQuizzesViewModel.quizzes.collectAsStateWithLifecycle()

    ContentLazyColumn { 
        items(quizzes) { quiz ->
            QuizCard(
                quiz = quiz,
                navigateToQuizDetails = {
                    navigateToQuizDetails(quiz.id!!)
                },
                changeFavoriteStatus = {
                    favoritesQuizzesViewModel.changeFavoriteStatus(quiz.id!!)
                }
            )
        }
    }
}