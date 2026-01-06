package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel

@Composable
fun GameScreen(
    quizViewModel: QuizViewModel,
    navigateBack: () -> Unit
) {
    val quiz by quizViewModel.quiz.collectAsStateWithLifecycle()
    val questions = quiz?.questionList ?: emptyList()
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var correctAnswers by remember { mutableIntStateOf(0) }

    AnimatedContent(
        targetState = currentQuestionIndex,
        transitionSpec = {
            slideInHorizontally { fullWidth -> fullWidth } togetherWith
                    slideOutHorizontally { fullWidth -> -fullWidth }
        }
    ) { targetIndex ->
        if (targetIndex >= questions.size) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Koniec quizu")
                Button(onClick = {
                    navigateBack()
                }) {
                    Text("Wroc")
                }
                Text(text = "Poprawnie $correctAnswers / ${questions.size}")
            }
        } else {
            val currentQuestion = questions[targetIndex]
            val answerList = currentQuestion.answerList ?: emptyList()
            
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "${currentQuestion.questionNumber}. ${currentQuestion.questionContent}")

                Spacer(modifier = Modifier.height(8.dp))
                
                if (answerList.size == 1) {
                    var inputValue by remember { mutableStateOf("") }
                    
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = {
                            inputValue = it
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    currentQuestionIndex++
                                    if (inputValue == answerList.first().answerContent) {
                                        correctAnswers++
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowRightAlt,
                                    contentDescription = "Strzalka do akceptacji"
                                )
                            }
                        }
                    )
                } else {
                    answerList.forEach { answer ->
                        Button(
                            onClick = {
                                currentQuestionIndex++
                                if (answer.isCorrect) {
                                    correctAnswers++
                                }
                            }
                        ) {
                            Text(text = answer.answerContent)
                        }
                    }
                }
            }
        }
    }

}