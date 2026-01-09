package com.github.projektmagma.magmaquiz.app.home.presentation.screens.quizzes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.app.home.presentation.CreateQuizViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.model.AnswerState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.QuestionState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand

@Composable
fun CreateQuestionScreen(
    isMultiple: Boolean,
    navigateBack: () -> Unit,
    createQuizViewModel: CreateQuizViewModel
) {
    var questionState by remember(isMultiple) {
        val initialState = if (isMultiple) {
            QuestionState(answerList = listOf(AnswerState(), AnswerState(), AnswerState(), AnswerState()))
        } else {
            QuestionState(answerList = listOf(AnswerState(isCorrect = true)))
        }
        mutableStateOf(initialState)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = questionState.content,
            onValueChange = {
                questionState = questionState.copy(content = it)
            }
        )
        
        if (isMultiple) { 
            questionState.answerList.forEachIndexed { index, answer ->
                Row {
                    OutlinedTextField(
                        value = answer.content,
                        onValueChange = {
                            questionState = questionState.copy(
                                answerList = questionState.answerList.mapIndexed { i, a -> 
                                    if (i == index) a.copy(content = it) else a
                                }
                            )
                        }
                    )
                    Checkbox(
                        checked = answer.isCorrect,
                        onCheckedChange = {
                            questionState = questionState.copy(
                                answerList = questionState.answerList.mapIndexed { i, a -> 
                                    if (i == index) a.copy(isCorrect = it) else a
                                }
                            )
                        }
                    )
                }
            }
            
        } else {
            OutlinedTextField(
                value = questionState.answerList.first().content,
                onValueChange = {
                    val updatedAnswer = questionState.answerList.first().copy(content = it)
                    questionState = questionState.copy(answerList = listOf(updatedAnswer))
                }
            )
        }
        
        Button(onClick = {
            createQuizViewModel.onCommand(QuizCommand.AddNewQuestion(questionState))
            navigateBack()
        }) {
            Text(text = "Zapisz pytanie")
        }
    }
}