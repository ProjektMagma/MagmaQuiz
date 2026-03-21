package com.github.projektmagma.magmaquiz.app.settings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.core.util.compressImage
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.settings.SettingsCommand
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.settings.SettingsState
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.no_image_provided_error
import magmaquiz.composeapp.generated.resources.profile_picture_changed_success

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiChannel = Channel<UiEvent>()
    val uiChannel = _uiChannel.receiveAsFlow()

    var state by mutableStateOf(SettingsState())

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
            settingsRepository.changeProfilePicture(
                profilePictureBig = profilePicture.compressImage(75, 512)!!,
                profilePictureSmall = profilePicture.compressImage(75, 128)!!
            )
                .whenSuccess {
                    _uiChannel.trySend(UiEvent.ShowSnackbar(Res.string.profile_picture_changed_success))
                }
                .whenError {
                    _uiChannel.trySend(UiEvent.ShowSnackbar(it.error.toResId()))
                }
        }
    }
}