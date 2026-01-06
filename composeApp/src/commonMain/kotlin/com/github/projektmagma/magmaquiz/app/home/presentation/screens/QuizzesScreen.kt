package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ContentLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCard
import com.github.projektmagma.magmaquiz.app.home.presentation.components.SearchTextField
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun QuizzesScreen(
    navigateToFavoritesScreen: () -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val quizzes by quizzesListViewModel.quizzes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            searchedText = quizzesListViewModel.quizName,
            labelText = "Quiz title",
            onSearch = {
                quizzesListViewModel.getQuizByName()
            },
            onValueChange = {
                quizzesListViewModel.quizName = it
                quizzesListViewModel.getQuizByName(true)
            }
        )
        TextButton(
            onClick = {
                navigateToFavoritesScreen()
            },
        ) {
            Text(text = "Favorites")
        }
        ContentLazyColumn {
            items(quizzes) { quiz ->
                QuizCard(
                    quiz = quiz,
                    navigateToQuizDetails = {
                        navigateToQuizDetails(quiz.id!!)
                    },
                    changeFavoriteStatus = {
                        quizzesListViewModel.changeFavoriteStatus(quiz.id!!)
                    }
                )
            }
        }
    }

}