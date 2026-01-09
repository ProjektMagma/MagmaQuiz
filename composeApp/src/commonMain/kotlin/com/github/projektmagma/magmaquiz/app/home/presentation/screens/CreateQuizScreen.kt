package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuestionCard
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand
import io.github.ismoy.imagepickerkmp.domain.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(
    createQuizViewModel: CreateQuizViewModel = koinViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val state = createQuizViewModel.state

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            
            Button(
                onClick = {
                    createQuizViewModel.onCommand(QuizCommand.Create)
                }
            ) {
                Text(text = "dodaj")
            }
            
            OutlinedTextField(
                value = state.name,
                onValueChange = { createQuizViewModel.onCommand(QuizCommand.NameChanged(it)) },
                placeholder = { Text(text = "name") }
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { createQuizViewModel.onCommand(QuizCommand.DescriptionChanged(it)) },
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
                            createQuizViewModel.onCommand(QuizCommand.VisibilityChanged(true))
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Prywatny") },
                        onClick = {
                            createQuizViewModel.onCommand(QuizCommand.VisibilityChanged(false))
                            expanded = false
                        }
                    )
                }
            }

            Button(
                onClick = {
                    showGallery = true
                }
            ) {
                Text(text = "wybierz zdjecie")
            }

            if (showGallery) {
                GalleryPickerLauncher(
                    onPhotosSelected = { photos ->
                        val bytes = photos.first().loadBytes()
                        createQuizViewModel.onCommand(QuizCommand.ImageChanged(bytes))
                        selectedImageBytes = bytes
                        showGallery = false
                    },
                    onError = { showGallery = false },
                    onDismiss = { showGallery = false },
                    allowMultiple = false
                )
            }

            if (selectedImageBytes != null) {
                AsyncImage(
                    model = selectedImageBytes,
                    contentDescription = "Zdjecie quizu"
                )
            }
        }

        items(state.questionList) { question ->
            QuestionCard(
                question = question,
                onQuestionContentChange = {
                    createQuizViewModel.onCommand(
                        QuizCommand.QuestionContentChanged(
                            it, question.id
                        )
                    )
                },
                onAnswerContentChange = {
                    createQuizViewModel.onCommand(QuizCommand.AnswerContentChanged(
                        it.content, question.id, it.id 
                    ))
                },
                onAddNewAnswer = {
                    createQuizViewModel.onCommand(
                        QuizCommand.AddNewAnswer(
                            question.id
                        )
                    )
                }
            )
        }

        item {
            IconButton(
                onClick = {
                    createQuizViewModel.onCommand(QuizCommand.AddNewQuestion)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj"
                )
            }
        }
    }
}