package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.util.compressImage
import com.github.projektmagma.magmaquiz.app.home.data.QuizService
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.AnswerState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuestionState
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
            is QuizCommand.QuestionEditor -> questionOptions(quizCommand)
            is QuizCommand.QuizProperties -> quizOptions(quizCommand)
            QuizCommand.CreateQuiz -> createQuiz()
        }
    }
    
    private fun quizOptions(command: QuizCommand.QuizProperties) {
        state = when (command) {
            is QuizCommand.QuizProperties.DescriptionChanged ->  state.copy(description = command.description)
            is QuizCommand.QuizProperties.ImageChanged -> state.copy(image = command.image)
            is QuizCommand.QuizProperties.NameChanged -> state.copy(name = command.name)
            is QuizCommand.QuizProperties.VisibilityChanged ->  state.copy(isPublic = command.isPublic)
        }
    }
    
    private fun questionOptions(command: QuizCommand.QuestionEditor) {
        when (command) {
            is QuizCommand.QuestionEditor.AnswerContentChanged -> updateAnswer(index = command.index) { it.copy(content = command.content) }
            is QuizCommand.QuestionEditor.AnswerCorrectnessChanged -> updateAnswer(index = command.index) { it.copy(isCorrect = command.isCorrect) }
            is QuizCommand.QuestionEditor.ContentChanged -> questionState = questionState.copy(content = command.content)
            is QuizCommand.QuestionEditor.ImageChanged -> questionState = questionState.copy(image = command.platformFile)
            is QuizCommand.QuestionEditor.Init -> {
                val nextNumber = state.questionList.size + 1
                questionState = if (command.isMultiple) {
                    QuestionState(
                        number = nextNumber,
                        answerList = List(4) { AnswerState() }
                    )
                } else {
                    QuestionState(
                        number = nextNumber,
                        answerList = listOf(AnswerState(isCorrect = true))
                    )
                }
            }
            is QuizCommand.QuestionEditor.SaveQuestion -> {
                val existingQuestionIndex = state.questionList.indexOfFirst { it.number == command.questionState.number }
                state = if (existingQuestionIndex != -1){
                    state.copy(
                        questionList = state.questionList.mapIndexed { index, question ->
                            if (index == existingQuestionIndex) command.questionState else question
                        }
                    )
                } else {
                    state.copy(
                        questionList = state.questionList + command.questionState
                    )
                }
            }
            is QuizCommand.QuestionEditor.SetForEditing -> questionState = command.questionState
        }
    }
    
    private inline fun updateAnswer(index: Int, transform: (AnswerState) -> AnswerState) {
        questionState = questionState.copy(
            answerList = questionState.answerList.mapIndexed { i, a -> 
                if (i == index) transform(a) else a 
            }
        )
    }

    private fun createQuiz() {
        viewModelScope.launch {
            quizService.createQuiz(
                CreateOrModifyQuizValue(
                    quizName = state.name,
                    quizDescription = state.description,
                    isPublic = state.isPublic,
                    quizImage = state.image.compressImage(75),
                    questionList = state.questionList.map { question ->
                        Question(
                            questionNumber = question.number,
                            questionContent = question.content,
                            questionImage = question.image.compressImage(75),
                            answerList = question.answerList.map {
                                Answer(
                                    answerContent = it.content,
                                    isCorrect = it.isCorrect
                                )
                            }
                        )
                    }
                )
            )
        }
    }
}