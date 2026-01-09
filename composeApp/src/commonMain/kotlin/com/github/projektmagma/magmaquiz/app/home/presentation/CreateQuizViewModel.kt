package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.home.data.QuizService
import com.github.projektmagma.magmaquiz.app.home.presentation.model.AnswerState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.QuestionState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizState
import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import kotlinx.coroutines.launch

class CreateQuizViewModel(
    private val quizService: QuizService
) : ViewModel() {
    var state by mutableStateOf(QuizState())

    fun onCommand(quizCommand: QuizCommand) {
        when (quizCommand) {
            is QuizCommand.DescriptionChanged -> state = state.copy(description = quizCommand.description)
            is QuizCommand.ImageChanged -> state = state.copy(image = quizCommand.byteArray)
            is QuizCommand.NameChanged -> state = state.copy(name = quizCommand.name)
            is QuizCommand.VisibilityChanged -> state = state.copy(isPublic = quizCommand.isPublic)
            
            QuizCommand.AddNewQuestion -> {
                state = state.copy(questionList = state.questionList + QuestionState())
            }
            is QuizCommand.QuestionContentChanged -> {
                val updatedList = state.questionList.map { 
                    if (it.id == quizCommand.questionId) {
                        it.copy(content = quizCommand.content)
                    } else { it }
                }
                state = state.copy(questionList = updatedList)
            }
            
            is QuizCommand.AddNewAnswer -> {
                val updatedQuestion = state.questionList.map {
                    if (it.id == quizCommand.questionId){
                        it.copy(answerList = it.answerList.plus(AnswerState()))
                    } else { it }
                }
                state = state.copy(questionList = updatedQuestion)
            }
            is QuizCommand.AnswerContentChanged -> {
                val updatedQuestions = state.questionList.map { 
                    if (it.id == quizCommand.questionId) {
                        val updateAnswers = it.answerList.map { answer ->
                            if (answer.id == quizCommand.answerId) {
                                answer.copy(content = quizCommand.content)
                            } else { answer }
                        }
                        it.copy(answerList = updateAnswers)
                    } else { it }
                }
                state = state.copy(questionList = updatedQuestions)
            }
            QuizCommand.Create -> createQuiz()
        }
    }

    private fun createQuiz() {
        viewModelScope.launch {
            quizService.createQuiz(
                CreateOrModifyQuizValue(
                quizName = state.name,
                quizDescription = state.description,
                isPublic = state.isPublic,
                quizImage = state.image,
                questionList = state.questionList.map { question ->
                    Question(
                        questionNumber = question.number,
                        questionContent = question.content,
                        answerList = question.answerList.map {
                            Answer(
                                answerContent = it.content,
                                isCorrect = it.isCorrect
                            )
                        }
                    )
                }
            ))
        }
    }
}