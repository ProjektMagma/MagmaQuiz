package com.github.projektmagma.magmaquiz.app.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeErrorIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.components.ProfilePictureIcon
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.home.presentation.components.FriendshipButtons
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCardSmall
import com.github.projektmagma.magmaquiz.app.users.presentation.UserDetailsViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersSharedViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

@Composable
fun UserDetailsScreen(
    id: UUID,
    navigateToEditScreen: (id: UUID) -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    userDetailsViewModel: UserDetailsViewModel = koinViewModel(),
    createQuizViewModel: CreateQuizViewModel = koinViewModel(),
    usersSharedViewModel: UsersSharedViewModel = koinViewModel()
) {
    val quizzes by userDetailsViewModel.quizzes.collectAsStateWithLifecycle()
    val user by usersSharedViewModel.user.collectAsStateWithLifecycle()
    val uiState by userDetailsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (user?.userId != id) {
            userDetailsViewModel.loadData(id)
        }
    }

    when (uiState) {
        is UiState.Error -> FullSizeErrorIndicator(
            message = (uiState as UiState.Error).errorMessage,
            onRetry = {
                userDetailsViewModel.loadData(id)
            }
        )

        UiState.Loading -> FullSizeCircularProgressIndicator()
        UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ProfilePictureIcon(
                                    imageData = user?.userProfilePicture,
                                    size = 64.dp
                                )
                                Text(
                                    text = user?.userName ?: "",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            if (userDetailsViewModel.checkOwnership(id)) {
                                IconButton(
                                    onClick = { navigateToSettingsScreen() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                            }
                        }

                        val foreignUser = user as? ForeignUser

                        if (foreignUser != null) {
                            FriendshipButtons(
                                user = foreignUser,
                                usersSharedViewModel
                            )
                        }
                    }
                }

                items(quizzes) { quiz ->

                    //TODO: Zmodyfikuj sobie tą composatkę aby były te opcje edycji
                    QuizCardSmall(
                        quiz = quiz,
                        showUserButton = false,
                        navigateToQuizDetails = { navigateToQuizDetails(quiz.id!!) },
                        changeFavoriteStatus = { userDetailsViewModel.changeFavoriteStatus(quiz.id!!) }
                    )

//                    Column(modifier = Modifier.fillMaxWidth()) {
//                        Row(
//                            modifier = Modifier
//                                .clickable(
//                                    onClick = {
//                                        navigateToQuizDetails(quiz.id!!)
//                                    }
//                                )
//                                .fillMaxWidth()
//                                .animateItem(
//                                    fadeOutSpec = tween(200),
//                                    fadeInSpec = tween(200),
//                                    placementSpec = tween(200)
//                                )
//                            ,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        ) {
//                            ContentImage(
//                                imageData = quiz.quizImage,
//                                imageSize = 96.dp
//                            )
//                            Column(
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text(
//                                    text = quiz.quizName,
//                                    style = MaterialTheme.typography.labelMedium
//                                )
//                                Text(text = quiz.quizDescription)
//                            }
//                            if (userDetailsViewModel.checkOwnership(id)) {
//                                Box{
//                                    IconButton(
//                                        onClick = {
//                                            expanded = !expanded
//                                        }
//                                    ) {
//                                        Icon(
//                                            imageVector = Icons.Default.MoreVert,
//                                            contentDescription = "more"
//                                        )
//                                    }
//                                    DropdownMenu(
//                                        expanded = expanded,
//                                        onDismissRequest = { expanded = false },
//                                    ) {
//                                        DropdownMenuItem(
//                                            text = { Text(stringResource(Res.string.edit)) },
//                                            onClick = {
//                                                createQuizViewModel.onCommand(QuizCommand.SetForEdit(quiz.id!!))
//                                                navigateToEditScreen(quiz.id!!)
//                                            }
//                                        )
//                                        DropdownMenuItem(
//                                            text = { Text(stringResource(Res.string.delete)) },
//                                            onClick = { userDetailsViewModel.deleteQuiz(quiz.id!!) }
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            IconButton(
//                                onClick = {
//                                    userDetailsViewModel.changeFavoriteStatus(quiz.id!!)
//                                }
//                            ) {
//                                Icon(
//                                    imageVector = if (quizzes.first { it.id == quiz.id!! }.likedByYou) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
//                                    tint = Color(0xfff498ae),
//                                    contentDescription = "FavoriteButton"
//                                )
//                            }
//                            Text(text = "${quizzes.first { it.id == quiz.id!! }.likesCount}")
//                        }
//                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}