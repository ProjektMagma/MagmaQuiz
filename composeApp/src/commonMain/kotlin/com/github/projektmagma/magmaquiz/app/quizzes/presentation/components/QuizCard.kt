package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.*

@Composable
expect fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    changeFavoriteStatus: () -> Unit
)