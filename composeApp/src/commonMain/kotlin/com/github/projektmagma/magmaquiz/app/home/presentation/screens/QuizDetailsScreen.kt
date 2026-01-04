package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun QuizDetailsScreen(
    id: UUID,
    quizDetailsViewModel: QuizDetailsViewModel = koinViewModel()
) {
    LaunchedEffect(id) {
        quizDetailsViewModel.getQuizById(id)
    }

    val quiz by quizDetailsViewModel.quiz.collectAsStateWithLifecycle()
    val currentQuiz = quiz

    if (currentQuiz == null) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = currentQuiz.quizName)
            Text(text = currentQuiz.quizDescription ?: "Brak opisu")
            Text(text = "Publiczny: ${currentQuiz.isPublic}")
            Text(text = "Autor: ${currentQuiz.quizCreatorName ?: "Nieznany"}")
            Text(text = "Polubienia: ${currentQuiz.likesCount}")
            Text(text = "Polubiony przez Ciebie: ${currentQuiz.likedByYou}")

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(currentQuiz.questionList.orEmpty()) { question ->
                    Text(text = "Pytanie nr: ${question.questionNumber}")
                    Text(text = question.questionContent)

                    question.answerList?.forEach { (id, answerContent, isCorrect) ->
                        Text(text = answerContent)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
