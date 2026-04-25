package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.game.presentation.GameMultiplayerViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionNumber
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.correct_answer
import magmaquiz.composeapp.generated.resources.correct_points
import magmaquiz.composeapp.generated.resources.end_game
import magmaquiz.composeapp.generated.resources.end_of_game
import magmaquiz.composeapp.generated.resources.leave_room
import magmaquiz.composeapp.generated.resources.left_seconds
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun GameMultiplayerScreen(
    gameQuizViewModel: GameMultiplayerViewModel = koinViewModel(),
    navigateOnGameFinish: () -> Unit
) {
    val gameState by gameQuizViewModel.gameState.collectAsStateWithLifecycle()
    val correctQuestions by gameQuizViewModel.correctQuestions.collectAsStateWithLifecycle()

    val backState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )

    NavigationEventHandler(
        state = backState,
        isBackEnabled = gameState.isQuizFinished,
        onBackCompleted = {
            gameQuizViewModel.onCommand(GameCommand.FinishGame)
            navigateOnGameFinish()
        }
    )

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
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (gameState.isQuizFinished) {
                if (gameQuizViewModel.checkIsHost()) {
                    Text(
                        text = stringResource(Res.string.end_of_game),
                        style = MaterialTheme.typography.titleLarge
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    ) {
                        items(correctQuestions.toList()) { (user, points) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    ProfilePictureIcon(
                                        imageData = user.userProfilePicture
                                    )
                                    Text(user.userName)
                                }
                                Text("$points / ${gameState.totalQuestions}")
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            navigateOnGameFinish()
                        }
                    ) {
                        Text(stringResource(Res.string.end_game))
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.end_of_game),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = stringResource(Res.string.correct_points, currentState.score, currentState.totalQuestions))
                    Text(
                        text = "${((currentState.score.toDouble() / currentState.totalQuestions.toDouble()) * 100).roundToInt()}%"
                    )
                    OutlinedButton(
                        onClick = {
                            gameQuizViewModel.sendMessage(message = WebSocketMessages.IncomingMessage.Disconnect)
                            navigateOnGameFinish()
                        }
                    ) {
                        Text(stringResource(Res.string.leave_room))
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.left_seconds, currentState.remainingSeconds),
                    style = MaterialTheme.typography.bodyMedium
                )

                ContentImage(imageData = currentState.questionImage)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuestionNumber(currentState.questionNumber)
                    Text(
                        text = currentState.questionContent,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (currentState.answers.size == 1) {
                    var inputValue by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .heightIn(min = 36.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AnimatedVisibility(
                                visible = currentState.isAnswered,
                                enter = slideInVertically { it } + fadeIn()
                            ) {
                                Text(
                                    text = stringResource(Res.string.correct_answer, currentState.answers.first().content),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                )
                            }
                        }

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
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                } else {
                    currentState.answers.forEach { answer ->
                        Button(
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    currentState.isAnswered && answer.isCorrect -> MaterialTheme.colorScheme.primary
                                    currentState.isAnswered && answer.isSelected -> MaterialTheme.colorScheme.error
                                    currentState.isAnswered -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    else -> MaterialTheme.colorScheme.primary
                                },
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = answer.content)

                                AnimatedVisibility(
                                    visible = currentState.isAnswered,
                                    enter = fadeIn()
                                ) {
                                    if (answer.isCorrect)
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null)
                                    else if (answer.isSelected)
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null)

                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}