package com.github.projektmagma.magmaquiz.app.quizzes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.root.UiState
import com.github.projektmagma.magmaquiz.app.quizzes.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.quizzes.domain.mappers.toModel
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizReviewCommand
import com.github.projektmagma.magmaquiz.app.quizzes.presentation.model.QuizReviewState
import com.github.projektmagma.magmaquiz.shared.data.domain.QuizReview
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class QuizReviewsViewModel(
    private val id: UUID,
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(QuizReviewState())
    val state = _state.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    init {
        getReviews()
    }
    
    fun onCommand(command: QuizReviewCommand){
        when (command){
            is QuizReviewCommand.ContentChanged -> _state.update { it.copy(content = command.newContent) }
            QuizReviewCommand.CreateReview -> createReview()
            is QuizReviewCommand.GetReviews -> getReviews()
            is QuizReviewCommand.RatingChanged -> _state.update { it.copy(rating = command.newRating) }
            is QuizReviewCommand.DeleteReview -> deleteReview(command.id)
        }
    }
    
    fun checkOwnership(id: UUID): Boolean{
        return id == authRepository.thisUser.value?.userId
    }
    
    private fun getReviews(){
        viewModelScope.launch {
            quizRepository.getQuizReviews(id)
                .whenSuccess { result ->
                    _state.update { it.copy(reviews = result.data.map { review -> review.toModel() }) }
                    _uiState.value = UiState.Success
                }
        }
    }
    
    private fun createReview(){
        viewModelScope.launch { 
            val review = QuizReview(
                comment = _state.value.content,
                rating = _state.value.rating
            )
            
            quizRepository.createQuizReview(
                id,
                review
            ).whenSuccess { 
                _state.update { 
                    it.copy(
                        reviews = it.reviews + review.toModel(authRepository.thisUser.value!!),
                        rating = 0,
                        content = ""
                    )
                }
            }
        }
    }
    
    private fun deleteReview(id: UUID){
        viewModelScope.launch { 
            quizRepository.deleteQuizReview(id)
                .whenSuccess { 
                    _state.update { 
                        it.copy(
                            reviews = it.reviews.filter { review -> review.author?.userName != authRepository.thisUser.value?.userName}
                        )
                    }
                }
        }
    }
}