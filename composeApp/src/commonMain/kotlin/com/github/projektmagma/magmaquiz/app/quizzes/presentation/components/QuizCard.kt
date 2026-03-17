package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.UUID

@Composable
expect fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: () -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    navigateToQuizReviews: () -> Unit,
    changeFavoriteStatus: () -> Unit
)