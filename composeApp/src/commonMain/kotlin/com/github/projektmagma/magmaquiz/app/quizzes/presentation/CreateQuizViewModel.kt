package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent.ShowSnackbar
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.compressImage
import com.github.projektmagma.magmaquiz.app.core.util.withSearchDelay
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.toResId
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.validateQuestion
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.validateQuiz
import com.github.projektmagma.magmaquiz.app.quizzes.domain.validators.validateTag
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.AnswerModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.CreateQuizState
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuestionModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.QuizCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.create.toQuestionModel
import com.github.projektmagma.magmaquiz.shared.data.domain.Answer
import com.github.projektmagma.magmaquiz.shared.data.domain.Question
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import com.github.projektmagma.magmaquiz.shared.data.rest.values.CreateOrModifyQuizValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _state = quizRepository.createQuizState
    val state = _state.asStateFlow()
    
    private val _quizChannel = Channel<NetworkEvent>() 
    val quizChannel = _quizChannel.receiveAsFlow()
    
    private val _uiChannel = Channel<UiEvent>()
    val uiChannel = _uiChannel.receiveAsFlow()
    
    fun onCommand(quizCommand: QuizCommand) {
        when (quizCommand) {
            is QuizCommand.QuestionEditor -> questionOptions(quizCommand)
            is QuizCommand.QuizProperties -> quizOptions(quizCommand)
            QuizCommand.CreateQuiz -> {
                _state.update {
                    it.copy(quizError = validateQuiz(it.quizModel))
                }
                if (_state.value.quizError != null) {
                    viewModelScope.launch { 
                        _uiChannel.send(ShowSnackbar(_state.value.quizError?.toResId()))
                    }
                    return
                }
                uploadQuiz()
            }
            is QuizCommand.SetForEdit -> getQuizForEdit(quizCommand.id)
            QuizCommand.ResetState -> resetState()
            is QuizCommand.TagNameChanged -> {
                val name = quizCommand.name
                val trimmed = name.trim()
                _state.update { it.copy(tagName = trimmed) }

                if (name.lastOrNull() == ' ') {
                    addNewTag(trimmed)
                } else {
                    getTags()
                }
            }
            
            is QuizCommand.AddNewTag -> {
                addNewTag(quizCommand.tagName)
            }

            is QuizCommand.RemoveTag -> _state.update {
                it.copy(
                    quizModel = it.quizModel.copy(
                        tagList = it.quizModel.tagList.filter { name -> name != quizCommand.name }
                    ),
                    tagError = null
                )
            }
                
            QuizCommand.GetTags -> getTags()
        }
    }
    
    private fun quizOptions(command: QuizCommand.QuizProperties) {
        _state.update { 
            it.copy(quizModel =
                when (command) {
                    is QuizCommand.QuizProperties.DescriptionChanged -> it.quizModel.copy(description = command.description)
                    is QuizCommand.QuizProperties.ImageChanged -> it.quizModel.copy(image = command.image)
                    is QuizCommand.QuizProperties.NameChanged -> it.quizModel.copy(name = command.name)
                    is QuizCommand.QuizProperties.VisibilityChanged ->  it.quizModel.copy(visibility = command.visibility)
                }
            )
        }
    }
    
    private fun questionOptions(command: QuizCommand.QuestionEditor) {
        when (command) {
            is QuizCommand.QuestionEditor.AnswerContentChanged -> updateAnswer(index = command.index) { it.copy(content = command.content) }
            is QuizCommand.QuestionEditor.AnswerCorrectnessChanged -> updateAnswer(index = command.index) { it.copy(isCorrect = command.isCorrect) }
            is QuizCommand.QuestionEditor.ContentChanged -> _state.update { it.copy(questionModel = it.questionModel.copy(content = command.content)) }
            is QuizCommand.QuestionEditor.ImageChanged -> _state.update { it.copy(questionModel = it.questionModel.copy(image = command.byteArray)) }
            is QuizCommand.QuestionEditor.Init -> {
                val nextNumber = _state.value.quizModel.questionList.size + 1
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
                _state.update { it.copy(questionModel = newQuestionModel) }
            }
            is QuizCommand.QuestionEditor.SaveQuestion -> {
                _state.update { it.copy(questionError = validateQuestion(command.questionModel)) }
                if (_state.value.questionError != null) {
                    _uiChannel.trySend(ShowSnackbar(_state.value.questionError?.toResId()))
                    return
                }
                
                val existingQuestionIndex = _state.value.quizModel.questionList.indexOfFirst { it.number == command.questionModel.number }
                _state.update {
                    if (existingQuestionIndex != -1){
                        it.copy(
                            quizModel = it.quizModel.copy(
                                questionList = it.quizModel.questionList.mapIndexed { index, question ->
                                    if (index == existingQuestionIndex) command.questionModel else question
                                }
                            )
                        )
                    } else {
                        it.copy(
                            quizModel = it.quizModel.copy(
                                questionList = it.quizModel.questionList + command.questionModel
                            )
                        )
                    }
                }
                _uiChannel.trySend(UiEvent.NavigateBack)
            }
            is QuizCommand.QuestionEditor.RemoveAnswer -> {
                _state.update {
                    it.copy(
                        questionModel = it.questionModel.copy(
                            answerList = it.questionModel.answerList.filterIndexed { index, _ -> index != command.index }
                        )
                    )
                }
            }
            QuizCommand.QuestionEditor.AddAnswer -> {
                _state.update {
                    it.copy(
                        questionModel = it.questionModel.copy(
                            answerList = it.questionModel.answerList + AnswerModel()
                        )
                    )
                }
            }
            is QuizCommand.QuestionEditor.SetForEditing -> _state.update { it.copy(questionModel = command.questionModel) }
        }
    }
    
    private fun addNewTag(tagName: String){
        if (tagName.isNotEmpty()) {
            _state.update {
                it.copy(tagError = validateTag(
                    tagName,
                    _state.value.quizModel.tagList)
                )
            }

            if (_state.value.tagError == null){
                _state.update {
                    it.copy(
                        quizModel = it.quizModel.copy(
                            tagList = _state.value.quizModel.tagList.plus(tagName)
                        ),
                        tagName = "",
                        tagList = emptyList()
                    )
                }
            }
            
            getTags()
        }
    }
    
    private fun getTags(){
        viewModelScope.launch {
            withSearchDelay(true, 100) {
                quizRepository.getTags(_state.value.tagName)
                    .whenSuccess { result ->
                        val newList = result.data.filter { it.tagName !in _state.value.quizModel.tagList }
                        _state.update {
                            it.copy(tagList = newList)
                        }
                    }
            }
        }
    }
    
    private inline fun updateAnswer(index: Int, transform: (AnswerModel) -> AnswerModel) {
        _state.update {
            it.copy(
                questionModel = it.questionModel.copy(
                    answerList = it.questionModel.answerList.mapIndexed { i, a ->
                        if (i == index) transform(a) else a
                    }
                )
            )
        }
    }

    private fun uploadQuiz() {
        viewModelScope.launch {
            val value =
                CreateOrModifyQuizValue(
                    id = _state.value.quizModel.id,
                    quizName = _state.value.quizModel.name,
                    quizDescription = _state.value.quizModel.description,
                    visibility = _state.value.quizModel.visibility,
                    quizImage = _state.value.quizModel.image.compressImage(75),
                    questionList = _state.value.quizModel.questionList.map { question ->
                        Question(
                            id = question.id,
                            questionNumber = question.number,
                            questionContent = question.content,
                            questionImage = question.image.compressImage(75),
                            answerList = question.answerList.map {
                                Answer(
                                    id = it.id,
                                    answerContent = it.content,
                                    isCorrect = it.isCorrect
                                )
                            }
                        )
                    },
                    tagList = _state.value.quizModel.tagList
                )
            
            val result = if (state.value.isEditing) {
                quizRepository.modifyQuiz(value)
            } else { 
                quizRepository.createQuiz(value)
            }

            when (result) {
                is Resource.Error -> _quizChannel.send(NetworkEvent.Failure(result.error))
                is Resource.Success -> {
                    _quizChannel.send(NetworkEvent.Success)
                    _uiChannel.send(UiEvent.NavigateBack)
                    delay(400)
                    resetState()
                }
            }
        }
    }

    private fun getQuizForEdit(id: UUID) {
        _state.value = CreateQuizState(isLoading = true)
        viewModelScope.launch {
            when (val result = quizRepository.getQuizById(id)) {
                is Resource.Error -> { _quizChannel.send(NetworkEvent.Failure(result.error)) }
                is Resource.Success -> {
                    val quiz = result.data
                    _state.update {
                        it.copy(
                            quizModel = it.quizModel.copy(
                                id = quiz.id,
                                name = quiz.quizName,
                                description = quiz.quizDescription,
                                image = quiz.quizImage,
                                visibility = quiz.visibility,
                                questionList = quiz.questionList.map { question ->
                                    question.toQuestionModel()
                                },
                                tagList = quiz.tagList.map { tag -> tag.tagName }
                            ),
                            isEditing = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    private fun resetState() {
        _state.value = CreateQuizState()
    }
}