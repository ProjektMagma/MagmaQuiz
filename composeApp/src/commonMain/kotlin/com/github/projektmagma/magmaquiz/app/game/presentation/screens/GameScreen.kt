package com.github.projektmagma.magmaquiz.app.game.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.CustomWindowDraggableArea
import com.github.projektmagma.magmaquiz.app.game.presentation.GameQuizViewModel
import com.github.projektmagma.magmaquiz.app.game.presentation.components.AnswersList
import com.github.projektmagma.magmaquiz.app.game.presentation.components.OpenAnswerField
import com.github.projektmagma.magmaquiz.app.game.presentation.components.ProgressDots
import com.github.projektmagma.magmaquiz.app.game.presentation.components.QuestionGameCard
import com.github.projektmagma.magmaquiz.app.game.presentation.components.SingleFinishSection
import com.github.projektmagma.magmaquiz.app.game.presentation.model.play.GameCommand
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameScreen(
    gameQuizViewModel: GameQuizViewModel = koinViewModel(),
    navigateOnGameFinish: () -> Unit
) {
    val gameState by gameQuizViewModel.gameState.collectAsStateWithLifecycle()

    val backState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)

    NavigationEventHandler(
        state = backState,
        isBackEnabled = gameState.isQuizFinished,
        onBackCompleted = {
            gameQuizViewModel.onCommand(GameCommand.FinishGame)
            navigateOnGameFinish()
        }
    )

    CustomWindowDraggableArea()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (gameState.isQuizFinished) {
            SingleFinishSection(
                score = gameState.score,
                total = gameState.totalQuestions,
                onBack = {
                    gameQuizViewModel.onCommand(GameCommand.FinishGame)
                    navigateOnGameFinish()
                }
            )
        } else {
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
                            GameCommand.AnswerClicked(
                                isCorrect = answer.isCorrect,
                                content = value
                            )
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