package com.github.projektmagma.magmaquiz.app.quizzes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
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
import com.github.projektmagma.magmaquiz.app.quizzes.domain.mappers.toResId
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.toResId
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionCard
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuestionTypeDialog
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.components.QuizDataTextField
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizVisibility
import magmaquiz.composeapp.generated.resources.*
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
    var tagListExpanded by remember { mutableStateOf(false) }

    var showQuestionDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    val state by createQuizViewModel.state.collectAsStateWithLifecycle()
    val backState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    val quiz = state.quizModel

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
                is UiEvent.ShowSnackbar -> {
                    val message = if (event.id != null) getString(event.id) else ""
                    SnackbarController.onEvent(message)
                }
            }
        }
    }

    if (showAlertDialog) {
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

    if (showQuestionDialog) {
        QuestionTypeDialog(
            onClick = {
                showQuestionDialog = false
                createQuizViewModel.onCommand(QuizCommand.QuestionEditor.Init(it))
                navigateToQuestionCreate(it)
            },
            changeDialogVisibility = { showQuestionDialog = false }
        )
    }

    if (state.isLoading) {
        FullSizeCircularProgressIndicator()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.width(170.dp),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            showQuestionDialog = true
                        }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                            Text(text = stringResource(Res.string.add_question))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(Res.string.add_question)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier.width(170.dp),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            createQuizViewModel.onCommand(QuizCommand.CreateQuiz)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = stringResource(Res.string.save_quiz))
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(Res.string.save_icon)
                            )
                        }
                    }
                }
            }
            item {

                QuizCoverImage(
                    modifier = Modifier
                        .heightIn(min = 128.dp, max = 256.dp)
                        .widthIn(min = 128.dp, max = 512.dp)
                        .padding(vertical = 8.dp),
                    model = quiz.image,
                    onImageClick = {
                        createQuizViewModel.onCommand(QuizCommand.QuizProperties.ImageChanged(it))
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                            value = stringResource(quiz.visibility.toResId()),
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
                                    createQuizViewModel.onCommand(
                                        QuizCommand.QuizProperties.VisibilityChanged(
                                            QuizVisibility.Public
                                        )
                                    )
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.friend_only)) },
                                onClick = {
                                    createQuizViewModel.onCommand(
                                        QuizCommand.QuizProperties.VisibilityChanged(
                                            QuizVisibility.FriendsOnly
                                        )
                                    )
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.private)) },
                                onClick = {
                                    createQuizViewModel.onCommand(
                                        QuizCommand.QuizProperties.VisibilityChanged(
                                            QuizVisibility.Private
                                        )
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = tagListExpanded,
                        onExpandedChange = {
                            createQuizViewModel.onCommand(QuizCommand.GetTags)
                            tagListExpanded = true
                        }
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                            shape = MaterialTheme.shapes.small,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                            ) {
                                Text(
                                    text = stringResource(Res.string.tags),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    quiz.tagList.forEach { tag ->
                                        InputChip(
                                            selected = false,
                                            onClick = {},
                                            label = { Text(tag) },
                                            trailingIcon = {
                                                Icon(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable {
                                                            createQuizViewModel.onCommand(QuizCommand.RemoveTag(tag))
                                                            tagListExpanded = true
                                                        },
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove tag"
                                                )
                                            }
                                        )
                                    }

                                    BasicTextField(
                                        modifier = Modifier
                                            .widthIn(min = 80.dp)
                                            .weight(1f)
                                            .padding(vertical = 12.dp),
                                        value = state.tagName,
                                        onValueChange = {
                                            createQuizViewModel.onCommand(QuizCommand.TagNameChanged(it))
                                            tagListExpanded = true
                                        },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        singleLine = true,
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Done,
                                            autoCorrectEnabled = false
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                createQuizViewModel.onCommand(
                                                    QuizCommand.AddNewTag(state.tagName)
                                                )
                                            }
                                        ),
                                        decorationBox = { innerTextField ->
                                            Box {
                                                if (state.tagName.isEmpty()) {
                                                    Text(
                                                        text = stringResource(Res.string.add_tag),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                }

                                Text(
                                    modifier = Modifier.align(Alignment.End),
                                    text = "${state.quizModel.tagList.size} / 20"
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = tagListExpanded && state.tagList.isNotEmpty(),
                            onDismissRequest = { tagListExpanded = false }
                        ) {
                            state.tagList.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.tagName) },
                                    onClick = {
                                        createQuizViewModel.onCommand(QuizCommand.AddNewTag(it.tagName))
                                    },
                                    trailingIcon = { Text(it.quizzesCount.toString()) }
                                )
                            }
                        }
                    }

                    Text(
                        text = if (state.tagError != null) stringResource(state.tagError!!.toResId()) else "",
                        color = MaterialTheme.colorScheme.error
                    )
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
        }
    }
}