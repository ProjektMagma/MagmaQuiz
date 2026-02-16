package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.github.projektmagma.magmaquiz.app.core.presentation.components.FullSizeCircularProgressIndicator
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizDataTextField
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.add_question
import magmaquiz.composeapp.generated.resources.all_changes_remove
import magmaquiz.composeapp.generated.resources.are_you_sure
import magmaquiz.composeapp.generated.resources.choose_type
import magmaquiz.composeapp.generated.resources.description
import magmaquiz.composeapp.generated.resources.multi_answer
import magmaquiz.composeapp.generated.resources.name
import magmaquiz.composeapp.generated.resources.no
import magmaquiz.composeapp.generated.resources.private
import magmaquiz.composeapp.generated.resources.public
import magmaquiz.composeapp.generated.resources.save_icon
import magmaquiz.composeapp.generated.resources.save_quiz
import magmaquiz.composeapp.generated.resources.single_answer
import magmaquiz.composeapp.generated.resources.success_quiz_add
import magmaquiz.composeapp.generated.resources.visibility
import magmaquiz.composeapp.generated.resources.yes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(
    navigateToQuestionCreate: (Boolean) -> Unit,
    navigateBack: () -> Unit,
    createQuizViewModel: CreateQuizViewModel = koinViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    val state = createQuizViewModel.state.collectAsStateWithLifecycle()
    val backState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    val quiz = state.value.quizModel

    NavigationEventHandler(
        state = backState,
        onBackCompleted = { showAlertDialog = true }
    )

    LaunchedEffect(createQuizViewModel.quizChannel) {
        createQuizViewModel.quizChannel.collect { event -> 
            when (event) {
                is NetworkEvent.Failure -> SnackbarController.onEvent(getString(event.networkError.toResId()))
                NetworkEvent.Success -> {
                    SnackbarController.onEvent(getString(Res.string.success_quiz_add))
                }
            }
        }
    }
    
    LaunchedEffect(createQuizViewModel.uiChannel) {
        createQuizViewModel.uiChannel.collect { event ->
            when (event) {
                UiEvent.NavigateBack -> navigateBack()
                is UiEvent.ShowSnackbar ->{
                    val message = if (event.id != null) getString(event.id) else ""
                    SnackbarController.onEvent(message)
                }
            }
        }
    }
    
    if (showAlertDialog){
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text(text = stringResource(Res.string.are_you_sure)) },
            text = { Text(text = stringResource(Res.string.all_changes_remove)) },
            confirmButton = { 
                Button(
                    onClick = {
                        showAlertDialog = false
                        createQuizViewModel.onCommand(QuizCommand.ResetState)
                        navigateBack()
                    }
                ) {
                    Text(text = stringResource(Res.string.yes))
                }
            },
            dismissButton = { 
                Button(
                    onClick = { showAlertDialog = false } 
                ) {
                    Text(text = stringResource(Res.string.no))
                }
            }
        )
    }
    
    if (state.value.isLoading) {
        FullSizeCircularProgressIndicator()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            createQuizViewModel.onCommand(QuizCommand.CreateQuiz)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = stringResource(Res.string.save_icon))
                            Text(text = stringResource(Res.string.save_quiz))
                        }
                    }
                }

                QuizCoverImage(
                    modifier = Modifier.padding(vertical = 8.dp),
                    height = 128.dp,
                    model = quiz.image,
                    onImageClick = {
                        createQuizViewModel.onCommand(QuizCommand.QuizProperties.ImageChanged(it))
                    }
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuizDataTextField(
                        value = quiz.name,
                        placeholder = stringResource(Res.string.name),
                        onValueChange = { createQuizViewModel.onCommand(QuizCommand.QuizProperties.NameChanged(it)) },
                    )
                    QuizDataTextField(
                        value = quiz.description,
                        placeholder = stringResource(Res.string.description),
                        onValueChange = { createQuizViewModel.onCommand(QuizCommand.QuizProperties.DescriptionChanged(it)) }
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly = true,
                            value = stringResource(if (quiz.isPublic) Res.string.public else Res.string.private),
                            onValueChange = {},
                            label = { Text(text = stringResource(Res.string.visibility)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.public)) },
                                onClick = {
                                    createQuizViewModel.onCommand(QuizCommand.QuizProperties.VisibilityChanged(true))
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.private)) },
                                onClick = {
                                    createQuizViewModel.onCommand(QuizCommand.QuizProperties.VisibilityChanged(false))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            items(quiz.questionList) { question ->
                QuestionCard(
                    question = question,
                    navigateToQuestionCreate = {
                        createQuizViewModel.onCommand(QuizCommand.QuestionEditor.SetForEditing(question))
                        navigateToQuestionCreate(question.answerList.size > 1)
                    }
                )
            }

            item {
                Button(onClick = {
                    showBottomSheet = true
                }) {
                    Text(text = stringResource(Res.string.add_question))
                }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = modalBottomSheetState
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = stringResource(Res.string.choose_type))

                            Button(
                                onClick = {
                                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.Init(false))
                                    navigateToQuestionCreate(false)
                                }
                            ) {
                                Text(text = stringResource(Res.string.single_answer))
                            }

                            Button(
                                onClick = {
                                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.Init(true))
                                    navigateToQuestionCreate(true)
                                }
                            ) {
                                Text(text = stringResource(Res.string.multi_answer))
                            }
                        }
                    }
                }
            }
        }
    }
}