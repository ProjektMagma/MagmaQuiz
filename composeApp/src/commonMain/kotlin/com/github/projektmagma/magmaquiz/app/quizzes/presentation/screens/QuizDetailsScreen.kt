package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.favoritePink
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizDetailsViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionNumber
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizVisibilityIcon
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.TagList
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.details.QuizDetailsCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.play_single
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun QuizDetailsScreen(
    id: UUID,
    navigateToPlayScreen: () -> Unit,
    navigateToReviewsScreen: (id: UUID, reviewed: Boolean) -> Unit
) {
    val quizDetailsViewModel: QuizDetailsViewModel = koinViewModel { parametersOf(id) }
    val uiState by quizDetailsViewModel.uiState.collectAsStateWithLifecycle()
    
    val state by quizDetailsViewModel.state.collectAsStateWithLifecycle()
    val currentQuiz = state.quizzes.firstOrNull { it.id == id }
    
    when (uiState) {
        is UiState.Error -> {
            FullSizeErrorIndicator(
                onRetry = {
                    quizDetailsViewModel.onCommand(QuizDetailsCommand.GetQuizById)
                } 
            )
        }
        UiState.Loading -> { FullSizeCircularProgressIndicator() }
        UiState.Success -> {
            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            ContentImage(
                                imageData = state.quiz!!.quizImage,
                                imageSize = 128.dp,
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = state.quiz!!.quizName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ProfilePictureIcon(
                                        imageData = state.quiz!!.quizCreator!!.userProfilePicture,
                                        size = 32.dp
                                    )
                                    Text(
                                        text = state.quiz!!.quizCreator!!.userName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                QuizVisibilityIcon(
                                    quizVisibility = state.quiz!!.visibility
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { quizDetailsViewModel.onCommand(QuizDetailsCommand.ChangeFavoriteStatus) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (currentQuiz?.likedByYou == true)
                                            Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                        tint = favoritePink,
                                        contentDescription = "FavoriteButton"
                                    )
                                }
                                Text(
                                    text = "${currentQuiz?.likesCount ?: 0}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    IconButton(
                                        onClick = {
                                            navigateToReviewsScreen(state.quiz?.id!!, state.quiz!!.reviewedByYou)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Comment,
                                            contentDescription = null
                                        )
                                    }
                                    Text(
                                        text = state.quiz!!.reviewCount.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.quiz!!.quizDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TagList(
                                tagList = state.quiz!!.tagList,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            thickness = 0.5.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(state.quiz!!.questionList) { question ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 1.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ContentImage(
                                    imageData = question.questionImage,
                                    imageSize = 72.dp
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    QuestionNumber(question.questionNumber)
                                    Text(
                                        text = question.questionContent,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = {
                        navigateToPlayScreen()
                        quizDetailsViewModel.onCommand(QuizDetailsCommand.SetupQuizForGame)
                        quizDetailsViewModel.onCommand(QuizDetailsCommand.AddQuizToHistory)
                    }
                ) {
                    Text(text = stringResource(Res.string.play_single))
                }
            }
        }
    }
}