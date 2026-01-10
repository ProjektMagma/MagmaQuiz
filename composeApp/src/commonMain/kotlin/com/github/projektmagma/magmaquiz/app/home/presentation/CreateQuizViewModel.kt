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
    var questionState by mutableStateOf(QuestionState())

    fun onCommand(quizCommand: QuizCommand) {
        when (quizCommand) {
            is QuizCommand.DescriptionChanged -> state = state.copy(description = quizCommand.description)
            is QuizCommand.ImageChanged -> state = state.copy(image = quizCommand.byteArray)
            is QuizCommand.NameChanged -> state = state.copy(name = quizCommand.name)
            is QuizCommand.VisibilityChanged -> state = state.copy(isPublic = quizCommand.isPublic)

            is QuizCommand.InitQuestion -> {
                val nextNumber = state.questionList.size + 1
                questionState = if (quizCommand.isMultiple) {
                    QuestionState(
                        number = nextNumber,
                        answerList = listOf(
                            AnswerState(),
                            AnswerState(),
                            AnswerState(),
                            AnswerState()
                        )
                    )
                } else {
                    QuestionState(
                        number =nextNumber,
                        answerList = listOf(AnswerState(isCorrect = true))
                    )
                }
            }
            is QuizCommand.AnswerContentChanged -> {
                questionState = questionState.copy(
                    answerList = questionState.answerList.mapIndexed { index, answer -> 
                        if (index == quizCommand.index) answer.copy(content = quizCommand.content) else answer
                    }
                )
            }
            is QuizCommand.AnswerCorrectnessChanged -> {
                questionState = questionState.copy(
                    answerList = questionState.answerList.mapIndexed { index, answer ->
                        if (index == quizCommand.index) answer.copy(isCorrect = quizCommand.isCorrect) else answer
                    }
                )
            }

            is QuizCommand.QuestionContentChanged -> questionState = questionState.copy(content = quizCommand.content)
            is QuizCommand.SetEditingQuestion -> questionState = quizCommand.questionState
            is QuizCommand.AddNewQuestion -> {
                val existingQuestionIndex = state.questionList.indexOfFirst { it.number == quizCommand.questionState.number }
                state = if (existingQuestionIndex != -1){
                    state.copy(
                        questionList = state.questionList.mapIndexed { index, question ->
                            if (index == existingQuestionIndex) quizCommand.questionState else question
                        }
                    )
                } else {
                    state.copy(
                        questionList = state.questionList + quizCommand.questionState
                    )
                }
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