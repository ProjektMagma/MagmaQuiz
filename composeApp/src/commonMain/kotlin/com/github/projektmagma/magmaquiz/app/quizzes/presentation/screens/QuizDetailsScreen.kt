package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.CommentButton
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.favoritePink
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.QuizDetailsViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionNumber
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.TagList
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

    val quiz by quizDetailsViewModel.quiz.collectAsStateWithLifecycle()

    val state by quizDetailsViewModel.quizListState.collectAsStateWithLifecycle()
    val currentQuiz = state.quizzes.firstOrNull { it.id == id }

    if (quiz == null) {
        FullSizeCircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .widthIn(max = 1000.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ContentImage(
                            imageData = quiz!!.quizImage,
                            imageSize = 128.dp,
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = quiz!!.quizName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProfilePictureIcon(
                                    imageData = quiz!!.quizCreator!!.userProfilePicture,
                                    size = 32.dp
                                )
                                Text(text = quiz!!.quizCreator!!.userName)
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {
                                    quizDetailsViewModel.changeFavoriteStatus(id)
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentQuiz?.likedByYou == true)
                                        Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    tint = favoritePink,
                                    contentDescription = "FavoriteButton"
                                )
                            }
                            Text(text = "${currentQuiz?.likesCount ?: 0}")
                        }
                    }

                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = quiz!!.quizDescription,
                        style = MaterialTheme.typography.labelMedium
                    )

                    CommentButton(
                        navigateToQuizReviews = { navigateToReviewsScreen(
                            quiz?.id!!, 
                            quiz!!.reviewedByYou
                        ) },
                        reviewCount = quiz!!.reviewCount
                    )
                    
                    TagList(
                        tagList = quiz!!.tagList
                    )
                }

                items(quiz!!.questionList) { question ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ContentImage(
                            imageData = question.questionImage,
                            imageSize = 96.dp
                        )
                        Column {
                            QuestionNumber(question.questionNumber)
                            Text(text = question.questionContent)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigateToPlayScreen()
                    quizDetailsViewModel.addQuizToMyHistory(id)
                }
            ) {
                Text(text = stringResource(Res.string.play_single))
            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Button(
//                    modifier = Modifier.weight(0.5f),
//                    onClick = {
//                        navigateToPlayScreen()
//                        quizDetailsViewModel.addQuizToMyHistory(id)
//                    }
//                ) {
//                    Text(text = "Graj")
//                }
//                Button(
//                    modifier = Modifier.weight(0.5f),
//                    onClick = {
//                        navigateToPlayScreen()
//                    }
//                ) {
//                    Text(text = "Graj multi")
//                }
//            }

        }
    }
}

