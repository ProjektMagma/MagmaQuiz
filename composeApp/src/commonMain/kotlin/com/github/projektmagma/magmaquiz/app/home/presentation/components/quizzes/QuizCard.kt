package com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz
import java.util.UUID

@Composable
expect fun QuizCard(
    quiz: Quiz,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToUserDetails: (id: UUID) -> Unit,
    changeFavoriteStatus: () -> Unit,
    modifier: Modifier = Modifier
)