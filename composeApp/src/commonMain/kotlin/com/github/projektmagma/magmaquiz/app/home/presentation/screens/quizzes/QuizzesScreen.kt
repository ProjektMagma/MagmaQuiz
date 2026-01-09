package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizzesListViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.AutoScalableLazyColumn
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FilterButton
import com.github.projektmagma.magmaquiz.app.home.presentation.components.SearchTextField
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuizCard
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.favorites
import magmaquiz.composeapp.generated.resources.quiz_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun QuizzesScreen(
    navigateToCreateQuizScreen: () -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    quizzesListViewModel: QuizzesListViewModel = koinViewModel()
) {
    val quizzes by quizzesListViewModel.quizzes.collectAsStateWithLifecycle()
    var isOnFavorites by remember { quizzesListViewModel.isOnFavorites }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchTextField(
            searchedText = quizzesListViewModel.quizName,
            labelText = stringResource(Res.string.quiz_title),
            onSearch = {
                quizzesListViewModel.getQuizByName()
            },
            onValueChange = {
                quizzesListViewModel.quizName = it
                quizzesListViewModel.getQuizByName(true)
            },
            trailingIcon = {
                FilterButton(
                    selected = isOnFavorites,
                    onClick = {
                        isOnFavorites = !isOnFavorites
                        if (isOnFavorites) quizzesListViewModel.getMyFavorites()
                        else quizzesListViewModel.getQuizByName(false)
                    },
                    contentLabel = stringResource(Res.string.favorites),
                    contentIcon = Icons.Default.Favorite
                )
            }
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    navigateToCreateQuizScreen()
                },
            ) {
                Text(text = "Create")
            }
        }
        AutoScalableLazyColumn(quizzes) { quiz ->
            QuizCard(
                quiz = quiz,
                navigateToQuizDetails = {
                    navigateToQuizDetails(quiz.id!!)
                },
                navigateToUserDetails = {
                    // TODO: Nawigacja (na początek zakładka users a potem szczegóły jak będą)
                },
                changeFavoriteStatus = {
                    quizzesListViewModel.changeFavoriteStatus(quiz.id!!)
                }
            )
        }
    }
}
