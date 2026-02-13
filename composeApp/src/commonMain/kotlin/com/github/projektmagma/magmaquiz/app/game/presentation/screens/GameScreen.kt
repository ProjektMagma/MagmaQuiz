package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.game.presentation.GameQuizViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameCommand
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    gameQuizViewModel: GameQuizViewModel = koinViewModel(),
    navigateOnGameFinish: () -> Unit
) {
    val gameState by gameQuizViewModel.gameState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameQuizViewModel.onCommand(GameCommand.StartGame)
    }


    BackHandler(gameState.isQuizFinished) {
        navigateOnGameFinish()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CustomWindowDraggableArea()
        AnimatedContent(
            targetState = gameState,
            transitionSpec = {
                slideInHorizontally { fullWidth -> fullWidth } togetherWith
                        slideOutHorizontally { fullWidth -> -fullWidth }
            },
            contentKey = { state -> state.currentQuestionIndex }
        ) { currentState ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentState.isQuizFinished) {
                    Text(text = "Koniec quizu")
                    Button(onClick = {
                        navigateOnGameFinish()
                    }) {
                        Text("Wroc")
                    }
                    Text(text = "Poprawnie ${currentState.score} / ${currentState.totalQuestions}")
                } else {
                    ContentImage(imageData = currentState.questionImage)
                    Text(text = "${currentState.questionNumber}. ${currentState.questionContent}")

                    Spacer(modifier = Modifier.height(8.dp))

                    if (currentState.answers.size == 1) {
                        var inputValue by remember { mutableStateOf("") }


                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = {
                                inputValue = it
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        gameQuizViewModel.onCommand(GameCommand.AnswerClicked(content = inputValue))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowRightAlt,
                                        contentDescription = "Strzalka do akceptacji"
                                    )
                                }
                            }
                        )
                        AnimatedVisibility(
                            visible = currentState.isAnswered,
                            enter = fadeIn() + slideInVertically { it },
                            exit = fadeOut()
                        ) {
                            Text(
                                text = "Poprawna odpowiedź: ${currentState.answers.first().content}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 32.dp)
                            )
                        }

                    } else {
                        currentState.answers.forEach { answer ->
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        currentState.isAnswered && answer.isCorrect -> MaterialTheme.colorScheme.primaryContainer
                                        currentState.isAnswered && answer.isSelected -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                ),
                                onClick = {
                                    gameQuizViewModel.onCommand(
                                        GameCommand.AnswerClicked(
                                            answer.isCorrect,
                                            answer.content
                                        )
                                    )
                                }
                            ) {
                                Text(text = answer.content)
                            }
                        }
                    }
                }
            }
        }
    }
}