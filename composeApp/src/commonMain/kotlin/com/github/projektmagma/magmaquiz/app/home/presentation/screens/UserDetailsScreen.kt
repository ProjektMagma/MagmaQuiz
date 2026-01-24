package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.UserDetailsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.ContentImage
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuizCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.create.QuizCommand
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@Composable
fun UserDetailsScreen(
    id: UUID,
    userDetailsViewModel: UserDetailsViewModel = koinViewModel(),
    createQuizViewModel: CreateQuizViewModel,
    navigateToEditScreen: (id: UUID) -> Unit,
    navigateToQuizDetails: (id: UUID) -> Unit,
    navigateToSettingsScreen: () -> Unit
) {
    val quizzes by userDetailsViewModel.quizzes.collectAsStateWithLifecycle()
    val user by userDetailsViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        if (user?.userId != id) {
            userDetailsViewModel.getQuizzesByUserId(id)
            userDetailsViewModel.getUserData(id)       
        }
    }

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
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = user?.userName ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
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
            }   
        }

        item {
            ContentImage(user?.userProfilePicture)
        }
        
        items(quizzes) { quiz ->
            QuizCard(
                quiz = quiz,
                navigateToQuizDetails = { navigateToQuizDetails(quiz.id!!) },
                navigateToUserDetails = { },
                changeFavoriteStatus = { userDetailsViewModel.changeFavoriteStatus(quiz.id!!) },
                modifier = Modifier.animateItem(
                    fadeOutSpec = tween(200),
                    fadeInSpec = tween(200),
                    placementSpec = tween(200)
                )
            )
            if (userDetailsViewModel.checkOwnership(id)) {
                Button(
                    onClick = { 
                        createQuizViewModel.onCommand(QuizCommand.SetForEdit(quiz.id!!))
                        navigateToEditScreen(quiz.id!!) 
                    }
                ) {
                    Text("Edytuj")
                }
                Button(
                    onClick = {
                        userDetailsViewModel.deleteQuiz(quiz.id!!)
                    }
                ) {
                    Text("Usun")
                }
            }
        }
    }
}