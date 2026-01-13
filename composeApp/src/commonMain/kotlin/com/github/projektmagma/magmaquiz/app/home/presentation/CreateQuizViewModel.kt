package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.compressImage
import com.github.projektmagma.magmaquiz.app.home.data.service.QuizService
import com.github.projektmagma.magmaquiz.app.home.domain.validators.toResId
import com.github.projektmagma.magmaquiz.app.home.domain.validators.validateQuestion
import com.github.projektmagma.magmaquiz.app.home.domain.validators.validateQuiz
import com.github.projektmagma.magmaquiz.app.home.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.AnswerModel
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.CreateQuizState
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuestionModel
import com.github.projektmagma.magmaquiz.app.home.presentation.model.quizzes.QuizCommand
import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateQuizViewModel(
    private val quizService: QuizService
) : ViewModel() {
    var state by mutableStateOf(CreateQuizState())
    
    private val _quizChannel = Channel<NetworkEvent>() 
    val quizChannel = _quizChannel.receiveAsFlow()
    
    private val _uiChannel = Channel<UiEvent>()
    val uiChannel = _uiChannel.receiveAsFlow()
    

    fun onCommand(quizCommand: QuizCommand) {
        when (quizCommand) {
            is QuizCommand.QuestionEditor -> questionOptions(quizCommand)
            is QuizCommand.QuizProperties -> quizOptions(quizCommand)
            QuizCommand.CreateQuiz -> {
                state = state.copy(quizError = validateQuiz(state.quizModel))
                if (state.quizError != null) {
                    viewModelScope.launch { 
                        _uiChannel.send(UiEvent.ShowSnackbar(state.quizError?.toResId()))
                    }
                    return
                }
                createQuiz()
            }
        }
    }
    
    private fun quizOptions(command: QuizCommand.QuizProperties) {
        state = state.copy(quizModel = when (command) {
            is QuizCommand.QuizProperties.DescriptionChanged -> state.quizModel.copy(description = command.description)
            is QuizCommand.QuizProperties.ImageChanged -> state.quizModel.copy(image = command.image)
            is QuizCommand.QuizProperties.NameChanged -> state.quizModel.copy(name = command.name)
            is QuizCommand.QuizProperties.VisibilityChanged ->  state.quizModel.copy(isPublic = command.isPublic)
        })
    }
    
    private fun questionOptions(command: QuizCommand.QuestionEditor) {
        when (command) {
            is QuizCommand.QuestionEditor.AnswerContentChanged -> updateAnswer(index = command.index) { it.copy(content = command.content) }
            is QuizCommand.QuestionEditor.AnswerCorrectnessChanged -> updateAnswer(index = command.index) { it.copy(isCorrect = command.isCorrect) }
            is QuizCommand.QuestionEditor.ContentChanged -> state = state.copy(questionModel = state.questionModel.copy(content = command.content))
            is QuizCommand.QuestionEditor.ImageChanged -> state = state.copy(questionModel = state.questionModel.copy(image = command.platformFile))
            is QuizCommand.QuestionEditor.Init -> {
                val nextNumber = state.quizModel.questionList.size + 1
                val newQuestionModel = if (command.isMultiple) {
                    QuestionModel(
                        number = nextNumber,
                        answerList = List(4) { AnswerModel() }
                    )
                } else {
                    QuestionModel(
                        number = nextNumber,
                        answerList = listOf(AnswerModel(isCorrect = true))
                    )
                }
                state = state.copy(questionModel = newQuestionModel)
            }
            is QuizCommand.QuestionEditor.SaveQuestion -> {
                state = state.copy(questionError = validateQuestion(command.questionModel))
                if (state.questionError != null) {
                    _uiChannel.trySend(UiEvent.ShowSnackbar(state.questionError?.toResId()))
                    return
                }
                
                val existingQuestionIndex = state.quizModel.questionList.indexOfFirst { it.number == command.questionModel.number }
                state = if (existingQuestionIndex != -1){
                    state.copy(
                        quizModel = state.quizModel.copy(
                            questionList = state.quizModel.questionList.mapIndexed { index, question ->
                                if (index == existingQuestionIndex) command.questionModel else question
                            }
                        )
                    )
                } else {
                    state.copy(
                        quizModel = state.quizModel.copy(
                            questionList = state.quizModel.questionList + command.questionModel
                        )
                    )
                }
                _uiChannel.trySend(UiEvent.NavigateBack)
            }
            is QuizCommand.QuestionEditor.RemoveAnswer -> {
                state = state.copy(
                    questionModel = state.questionModel.copy(
                        answerList = state.questionModel.answerList.filterIndexed { index, state -> index != command.index }
                    )
                )
            }
            QuizCommand.QuestionEditor.AddAnswer -> {
                state = state.copy(
                    questionModel = state.questionModel.copy(
                        answerList = state.questionModel.answerList + AnswerModel()
                    )
                )
            }
            is QuizCommand.QuestionEditor.SetForEditing -> state = state.copy(questionModel = command.questionModel)
        }
    }
    
    private inline fun updateAnswer(index: Int, transform: (AnswerModel) -> AnswerModel) {
        state = state.copy(
            questionModel = state.questionModel.copy(
                answerList = state.questionModel.answerList.mapIndexed { i, a -> 
                    if (i == index) transform(a) else a 
                }
            )
        )
    }

    private fun createQuiz() {
        viewModelScope.launch {
            val result = quizService.createQuiz(
                CreateOrModifyQuizValue(
                    quizName = state.quizModel.name,
                    quizDescription = state.quizModel.description,
                    isPublic = state.quizModel.isPublic,
                    quizImage = state.quizModel.image.compressImage(75),
                    questionList = state.quizModel.questionList.map { question ->
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
            
            when (result) {
                is Resource.Error -> _quizChannel.send(NetworkEvent.Failure(result.error)) 
                is Resource.Success -> {
                    clearState()
                    _quizChannel.send(NetworkEvent.Success)
                    _uiChannel.send(UiEvent.NavigateBack)
                }
            }
        }
    }
    
    fun clearState(){
        state = CreateQuizState()
    }
}