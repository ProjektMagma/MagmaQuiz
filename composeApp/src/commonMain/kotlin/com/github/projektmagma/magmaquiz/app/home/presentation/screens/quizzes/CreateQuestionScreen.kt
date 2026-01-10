package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.components.QuizCoverImage
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand

@Composable
fun CreateQuestionScreen(
    isMultiple: Boolean,
    navigateBack: () -> Unit,
    createQuizViewModel: CreateQuizViewModel
) {
    val questionState = createQuizViewModel.questionState
    
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizCoverImage(
            height = 256.dp,
            model = questionState.image,
            onImageClick = { createQuizViewModel.onCommand(QuizCommand.QuestionEditor.ImageChanged(it)) }
        )
        
        OutlinedTextField(
            value = questionState.content,
            onValueChange = {
                createQuizViewModel.onCommand(QuizCommand.QuestionEditor.ContentChanged(it))
            }
        )
        
        if (isMultiple) { 
            questionState.answerList.forEachIndexed { index, answer ->
                Row {
                    OutlinedTextField(
                        value = answer.content,
                        onValueChange = {
                            createQuizViewModel.onCommand(QuizCommand.QuestionEditor.AnswerContentChanged(it, index))
                        }
                    )
                    Checkbox(
                        checked = answer.isCorrect,
                        onCheckedChange = {
                            createQuizViewModel.onCommand(QuizCommand.QuestionEditor.AnswerCorrectnessChanged(it, index))
                        }
                    )
                }
            }
            
        } else {
            OutlinedTextField(
                value = questionState.answerList.first().content,
                onValueChange = {
                    createQuizViewModel.onCommand(QuizCommand.QuestionEditor.AnswerContentChanged(it, 0))
                }
            )
        }
        
        Button(onClick = {
            createQuizViewModel.onCommand(QuizCommand.QuestionEditor.SaveQuestion(questionState))
            navigateBack()
        }) {
            Text(text = "Zapisz pytanie")
        }
    }
}