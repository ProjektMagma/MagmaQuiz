package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.content
import magmaquiz.composeapp.generated.resources.delete_answer
import magmaquiz.composeapp.generated.resources.save_question
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateQuestionScreen(
    isMultiple: Boolean,
    navigateBack: () -> Unit,
    createQuizViewModel: CreateQuizViewModel = koinViewModel()
) {
    val state = createQuizViewModel.state.collectAsStateWithLifecycle()
    val question = state.value.questionModel
    
    LaunchedEffect(createQuizViewModel.uiChannel){
        createQuizViewModel.uiChannel.collect { event -> 
            when (event) {
                UiEvent.NavigateBack -> navigateBack()
                is UiEvent.ShowSnackbar -> {
                    val message = if (event.id != null) getString(event.id) else ""
                    SnackbarController.onEvent(message)
                }
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            QuizCoverImage(
                height = 196.dp,
                model = question.image,
                onImageClick = {
                    createQuizViewModel.onCommand(
                        QuizCommand.QuestionEditor.ImageChanged(it)
                    )
                }
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = question.content,
                onValueChange = {
                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.ContentChanged(it))
                },
                placeholder = { Text(text = stringResource(Res.string.content)) },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
        if (isMultiple) {
            itemsIndexed(question.answerList) { index, answer ->
                Row {
                    IconButton(
                        onClick = {
                            createQuizViewModel.onCommand(
                                QuizCommand.QuestionEditor.RemoveAnswer(
                                    index
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_answer)
                        )
                    }
                    OutlinedTextField(
                        value = answer.content,
                        onValueChange = {
                            createQuizViewModel.onCommand(
                                QuizCommand.QuestionEditor.AnswerContentChanged(it, index)
                            )
                        }
                    )
                    Checkbox(
                        checked = answer.isCorrect,
                        onCheckedChange = {
                            createQuizViewModel.onCommand(
                                QuizCommand.QuestionEditor.AnswerCorrectnessChanged(it, index)
                            )
                        }
                    )
                }
            }
            item {
                if (question.answerList.size < 4) {
                    Column(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.AddAnswer)
                                }
                            )
                    ) {
                        Row {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                            Text("Add option")
                        }
                    }
                }
            }
        } else {
            item {
                OutlinedTextField(
                    value = question.answerList.first().content,
                    onValueChange = {
                        createQuizViewModel.onCommand(
                            QuizCommand.QuestionEditor.AnswerContentChanged(it, 0)
                        )
                    }
                )
            }
        }
        item {
            Button(onClick = {
                createQuizViewModel.onCommand(QuizCommand.QuestionEditor.SaveQuestion(question))
            }) {
                Text(text = stringResource(Res.string.save_question))
            }
        }
    }
}