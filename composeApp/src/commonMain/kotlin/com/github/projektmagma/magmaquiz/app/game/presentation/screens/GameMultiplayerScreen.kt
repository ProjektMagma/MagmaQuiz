package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.github.projektmagma.magmaquiz.app.core.util.ObserveAsEvents
import com.github.projektmagma.magmaquiz.app.game.presentation.GameMultiplayerViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.components.AnswersList
import com.github.projektmagma.magmaquiz.app.game.presentation.components.FinishSection
import com.github.projektmagma.magmaquiz.app.game.presentation.components.OpenAnswerField
import com.github.projektmagma.magmaquiz.app.game.presentation.components.ProgressDots
import com.github.projektmagma.magmaquiz.app.game.presentation.components.QuestionGameCard
import com.github.projektmagma.magmaquiz.app.game.presentation.model.GameEvent
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.WebSocketMessages
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameMultiplayerScreen(
    gameQuizViewModel: GameMultiplayerViewModel = koinViewModel(),
    navigateOnGameFinish: () -> Unit
) {
    val gameState by gameQuizViewModel.gameState.collectAsStateWithLifecycle()
    val answers by gameQuizViewModel.selectedAnswers.collectAsStateWithLifecycle()
    val roomSettings by gameQuizViewModel.roomSettings.collectAsStateWithLifecycle()
    
    val backState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    var showCloseDialog by remember { mutableStateOf(false) }

    ObserveAsEvents(gameQuizViewModel.event) { event -> 
        when (event) {
            is GameEvent.Closed -> if (!gameState.isQuizFinished) {
                navigateOnGameFinish()
                showCloseDialog = true
            }
            GameEvent.Success -> Unit
        }
    }

    if (showCloseDialog){
        AlertDialog(
            confirmButton = {
                Button(
                    onClick = {
                        navigateOnGameFinish()
                    }
                ) {
                    Text("OK")
                }
            },
            onDismissRequest = { showCloseDialog = false },
            text = { Text("Host opuscil pokoj, gra zakonczona") }
        )
    }

    NavigationEventHandler(
        state = backState,
        isBackEnabled = gameState.isQuizFinished,
        onBackCompleted = {
            gameQuizViewModel.onCommand(GameCommand.FinishGame)
            navigateOnGameFinish()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (gameState.isQuizFinished) {
            FinishSection(
                answersMap = answers,
                questions = roomSettings!!.currentQuiz.questionList,
                score = gameState.score,
                total = gameState.totalQuestions,
                onBack = {
                    gameQuizViewModel.sendMessage(WebSocketMessages.IncomingMessage.Disconnect)
                    navigateOnGameFinish()
                }
            )
        } else {
            TimerBar(
                remainingSeconds = gameState.remainingSeconds,
                total = gameState.secondsForQuestion
            )
            ProgressDots(
                total = gameState.totalQuestions,
                current = gameState.currentQuestionIndex
            )
            QuestionGameCard(
                imageData = gameState.questionImage,
                content = gameState.questionContent
            )
            if (gameState.answers.size == 1) {
                val answer = gameState.answers.first()
                OpenAnswerField(
                    isAnswered = gameState.isAnswered,
                    correctAnswerContent = answer.content,
                    onSubmit = { value ->
                        gameQuizViewModel.onCommand(
                            GameCommand.AnswerClicked(content = value)
                        )
                    }
                )
            } else {
                AnswersList(
                    answers = gameState.answers,
                    isAnswered = gameState.isAnswered,
                    onAnswerClick = { answer ->
                        gameQuizViewModel.onCommand(
                            GameCommand.AnswerClicked(answer.isCorrect, answer.content)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TimerBar(remainingSeconds: Int, total: Int) {
    val fraction = (remainingSeconds / total.toFloat()).coerceIn(0f, 1f)
    val color = when {
        fraction > 0.5f -> MaterialTheme.colorScheme.tertiary
        fraction > 0.25f -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(500),
        label = "timer"
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinearProgressIndicator(
                progress = { animatedFraction },
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                text = "$remainingSeconds s",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.widthIn(min = 32.dp),
                textAlign = TextAlign.End
            )
        }
    }
}