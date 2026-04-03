package com.github.projektmagma.magmaquiz.app.quizzes.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility.FriendsOnly
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility.Private
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility.Public

@Composable
fun QuizVisibilityIcon(
    quizVisibility: QuizVisibility
) {
    val icon = when (quizVisibility) {
        Private -> Icons.Default.Lock
        FriendsOnly -> Icons.Default.Group
        Public -> Icons.Default.Public
    }
    
    Icon(
        imageVector = icon,
        contentDescription = null
    )
}