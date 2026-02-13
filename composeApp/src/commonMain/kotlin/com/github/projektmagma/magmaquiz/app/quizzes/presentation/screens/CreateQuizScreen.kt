package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
    val quiz = state.value.quizModel

    BackHandler {
        showAlertDialog = true
    }

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