package com.github.projektmagma.magmaquiz.app.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.home.data.repository.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.home.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.home.presentation.model.settings.SettingsCommand
import com.github.projektmagma.magmaquiz.app.home.presentation.model.settings.SettingsState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.no_image_provided_error
import magmaquiz.composeapp.generated.resources.profile_picture_changed_success

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiChannel = Channel<UiEvent>()
    val uiChannel = _uiChannel.receiveAsFlow()

    var state by mutableStateOf(SettingsState())

    init {
        getQuizzesByUserId()
    }

    fun onCommand(command: SettingsCommand) {
        when (command) {
            SettingsCommand.ChangeProfilePicture -> changeProfilePicture(state.profilePicture)
            is SettingsCommand.ImageChanged -> state = state.copy(profilePicture = command.profilePicture)
        }
    }

    private fun changeProfilePicture(profilePicture: ByteArray?) {
        if (profilePicture == null) {
            _uiChannel.trySend(UiEvent.ShowSnackbar(Res.string.no_image_provided_error))
            return
        }

        viewModelScope.launch {
            val smallImage = FileKit.compressImage(
                bytes = profilePicture,
                quality = 75,
                maxWidth = 128,
                maxHeight = 128,
                imageFormat = ImageFormat.JPEG
            )

            settingsRepository.changeProfilePicture(
                profilePictureBig = profilePicture,
                profilePictureSmall = smallImage
            )
                .whenSuccess {
                    _uiChannel.trySend(UiEvent.ShowSnackbar(Res.string.profile_picture_changed_success))
                }
                .whenError {
                    _uiChannel.trySend(UiEvent.ShowSnackbar(it.error.toResId()))
                }
        }
    }

    // TODO przechwytywanie bledow
    private fun getQuizzesByUserId() {
        viewModelScope.launch {
            quizRepository.getQuizzesByUserId(authRepository.thisUser.value?.userId!!).whenSuccess { state = state.copy(quizzes = it.data) }
        }
    }
}