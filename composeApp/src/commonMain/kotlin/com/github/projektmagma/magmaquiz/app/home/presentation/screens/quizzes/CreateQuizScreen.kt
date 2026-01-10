package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.home.presentation.components.quizzes.QuestionCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(
    navigateToQuestionCreate: (Boolean) -> Unit,
    createQuizViewModel: CreateQuizViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val state = createQuizViewModel.state

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
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
                    Text(text = "Zapisz quiz")
                }
            }

            QuizCoverImage(
                height = 128.dp,
                model = state.image,
                onImageClick = {
                    createQuizViewModel.onCommand(QuizCommand.QuizProperties.ImageChanged(it))
                }
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.name,
                    onValueChange = { createQuizViewModel.onCommand(QuizCommand.QuizProperties.NameChanged(it)) },
                    placeholder = { Text(text = "name") }
                )
                TextField(
                    value = state.description,
                    onValueChange = {
                        createQuizViewModel.onCommand(QuizCommand.QuizProperties.DescriptionChanged(it))
                    },
                    placeholder = { Text(text = "description") }
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        readOnly = true,
                        value = state.isPublic.toString(),
                        onValueChange = {},
                        label = { Text(text = "Widocznosc") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Publiczny") },
                            onClick = {
                                createQuizViewModel.onCommand(QuizCommand.QuizProperties.VisibilityChanged(true))
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Prywatny") },
                            onClick = {
                                createQuizViewModel.onCommand(QuizCommand.QuizProperties.VisibilityChanged(false))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        items(state.questionList) { question ->
            QuestionCard(
                question = question,
                navigateToQuestionCreate = {
                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.SetForEditing(question))
                    navigateToQuestionCreate(question.answerList.size>1)
                }
            )
        }

        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Button(onClick = {
                    showBottomSheet = true
                }) {
                    Text(text = "Dodaj pytanie")
                }
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
                        Text(text = "Wybierz typ pytania")
                        
                        Button(
                            onClick = {
                                createQuizViewModel.onCommand(QuizCommand.QuestionEditor.Init(false))
                                navigateToQuestionCreate(false)
                            }
                        ) {
                            Text(text = "Uzupelnianie luki")
                        }

                        Button(
                            onClick = {
                                createQuizViewModel.onCommand(QuizCommand.QuestionEditor.Init(true))
                                navigateToQuestionCreate(true)
                            }
                        ) {
                            Text(text = "Wielokrotna odpowiedz")
                        }
                    }
                }
            }
        }
    }
}