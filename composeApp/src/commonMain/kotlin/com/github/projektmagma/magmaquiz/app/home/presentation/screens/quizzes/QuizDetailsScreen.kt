package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.util.convertLongSecondsToString
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import java.util.UUID

@Composable
fun QuizDetailsScreen(
    id: UUID,
    quizViewModel: QuizViewModel,
    navigateToPlayScreen: () -> Unit,
    navigateBack: () -> Unit
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
            IconButton(
                onClick = { // na desktopie koniecznie musi być, bo inaczej escape usuwa z backstack co jest nieintuicyjne
                    navigateBack()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "BackButton",
                )
            }
            Text(text = "Tytul: ${currentQuiz.quizName}")
            Text(text = "Opis: ${currentQuiz.quizDescription}")
            Text(text = "Publiczny: ${currentQuiz.isPublic}")
            Text(text = "Autor: ${currentQuiz.quizCreator.userName}")
            Text(text = "Polubienia: ${currentQuiz.likesCount}")
            Text(text = "Polubiony przez Ciebie: ${currentQuiz.likedByYou}")

            // Pierwotnie to chciałem dać w card, ale się na androidzie nie skalowało, więc to tu zostawiam
            // EDIT: Już nieważne, ale nadal to tu zostawię
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Data stworzenia: ")
                Text(
                    text = convertLongSecondsToString(currentQuiz.createdAt),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Data modyfikacji: ")
                Text(
                    text = convertLongSecondsToString(currentQuiz.modifiedAt),
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    navigateToPlayScreen()
                }
            ) {
                Text(text = "Graj")
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(currentQuiz.questionList) { question ->
                    Text(text = "Pytanie nr: ${question.questionNumber}")
                    Text(text = question.questionContent)

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

