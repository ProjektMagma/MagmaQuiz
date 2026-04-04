package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizReviewsViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.CommentCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.review.QuizReviewCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.already_reviewed
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun QuizReviewsScreen(
    id: UUID,
    reviewed: Boolean,
    navigateToQuizDetails: (id: UUID) -> Unit
) {
    val quizReviewsViewModel: QuizReviewsViewModel = koinViewModel { parametersOf(id, reviewed) }
    val state by quizReviewsViewModel.state.collectAsStateWithLifecycle()
    val uiState by quizReviewsViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .widthIn(max = 1000.dp)
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f) 
                .fillMaxWidth()
        ) {
            when (uiState) {
                is UiState.Error -> { }
                UiState.Loading -> item { FullSizeCircularProgressIndicator() }
                UiState.Success -> {
                    items(state.reviews) {
                        CommentCard(
                            review = it,
                            showOptions = quizReviewsViewModel.checkOwnership(it.author?.userId!!),
                            deleteReview = { quizReviewsViewModel.onCommand(QuizReviewCommand.DeleteReview(id, it.rating)) },
                            navigateToUserDetails = { navigateToQuizDetails(it.author.userId!!) }
                        )
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            if (state.hasReviewed){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.already_reviewed),
                    textAlign = TextAlign.Center
                )
            } else {
                RatingInput(
                    rating = state.rating,
                    onRatingChange = { quizReviewsViewModel.onCommand(QuizReviewCommand.RatingChanged(it)) },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f), 
                        value = state.content,
                        onValueChange = { quizReviewsViewModel.onCommand(QuizReviewCommand.ContentChanged(it)) }
                    )
                    val canSubmit = state.rating > 0

                    IconButton(
                        enabled = canSubmit,
                        onClick = {
                            quizReviewsViewModel.onCommand(QuizReviewCommand.CreateReview)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            tint = if (canSubmit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }   
            }
        }
    }
}

@Composable
fun RatingInput(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    maxRating: Int = 5,
    starSize: Dp = 32.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Rounded.Star else Icons.Rounded.StarBorder
            val iconTintColor = if (isSelected) Color(0xFFFFB400) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

            IconButton(
                onClick = { onRatingChange(i) },
                modifier = Modifier.size(starSize + 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Ocena $i",
                    tint = iconTintColor,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}