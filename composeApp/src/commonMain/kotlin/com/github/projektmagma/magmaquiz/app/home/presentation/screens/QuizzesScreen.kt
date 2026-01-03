package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuizzesScreen(
    quizViewModel: QuizViewModel = koinViewModel()
) {
    val quizzes by quizViewModel.quizzes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Quizzes")
        OutlinedTextField(
            value = quizViewModel.quizName,
            onValueChange = { quizViewModel.quizName = it},
            trailingIcon = {
                IconButton(
                    onClick = {
                        quizViewModel.getQuizByName()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Szukaj"
                    )
                }
            }
        )
        LazyColumn {
            items(quizzes){ quiz ->
                QuizCard(quiz)
            }
        }
    }
    
}