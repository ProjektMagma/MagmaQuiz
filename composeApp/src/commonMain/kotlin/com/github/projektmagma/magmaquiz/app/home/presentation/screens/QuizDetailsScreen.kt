package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import java.util.UUID

@Composable
fun QuizDetailsScreen(
    id: UUID,
    quizViewModel: QuizViewModel,
    navigateToPlayScreen: () -> Unit
) {
    LaunchedEffect(id) {
        quizViewModel.getQuizById(id)
    }

    val quiz by quizViewModel.quiz.collectAsStateWithLifecycle()
    val currentQuiz = quiz
    
    if (currentQuiz == null) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Tytul: ${currentQuiz.quizName}")
            Text(text = "Opis: ${currentQuiz.quizDescription}")
            Text(text = "Publiczny: ${currentQuiz.isPublic}")
            Text(text = "Autor: ${currentQuiz.quizCreatorName ?: "Nieznany"}")
            Text(text = "Polubienia: ${currentQuiz.likesCount}")
            Text(text = "Polubiony przez Ciebie: ${currentQuiz.likedByYou}")

            Button(
                onClick = {
                    navigateToPlayScreen()
                }
            ) {
                Text(text = "Graj")
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(currentQuiz.questionList.orEmpty()) { question ->
                    Text(text = "Pytanie nr: ${question.questionNumber}")
                    Text(text = question.questionContent)

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

