package com.github.projektmagma.magmaquiz.app.settings.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CircleCropShape
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.filekit.encodeToByteArray
import com.attafitamim.krop.filekit.toImageSrc
import com.attafitamim.krop.ui.ImageCropperDialog
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.app.core.presentation.mappers.toResId
import com.github.projektmagma.magmaquiz.app.core.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.presentation.SettingsViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.components.SettingsOption
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.settings.SettingsCommand
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.account_details_change
import magmaquiz.composeapp.generated.resources.change_profile_picture
import magmaquiz.composeapp.generated.resources.location_details_change
import magmaquiz.composeapp.generated.resources.log_out
import magmaquiz.composeapp.generated.resources.no_image_provided_error
import magmaquiz.composeapp.generated.resources.save
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navigateToAuth: () -> Unit,
    navigateToChangeAccountDetailsScreen: () -> Unit,
    navigateToChangeLocationDetailsScreen: () -> Unit
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState

    val state = settingsViewModel.state

    LaunchedEffect(authViewModel.authChannel) {
        authViewModel.authChannel.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> {
                    SnackbarController.onEvent(getString(event.networkError.toResId()))
                }

                NetworkEvent.Success -> {
                    navigateToAuth()
                }
            }
        }
    }

    LaunchedEffect(settingsViewModel.uiChannel) {
        settingsViewModel.uiChannel.collect { event ->
            when (event) {
                UiEvent.NavigateBack -> {}
                is UiEvent.ShowSnackbar -> {
                    val message = if (event.id != null) getString(event.id) else ""
                    SnackbarController.onEvent(message)
                }
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(256.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .crossfade(true)
                        .data(state.profilePicture)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "ProfilePicture"
                )

                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        settingsViewModel.onCommand(SettingsCommand.ChangeProfilePicture)
                        showDialog = false
                    }) {
                    Text(text = stringResource(Res.string.save))
                }
            }
        }
    }

    if (cropState != null) ImageCropperDialog(
        state = cropState,
        style = cropperStyle(
            aspects = listOf(AspectRatio(1, 1)),
            shapes = listOf(CircleCropShape),
            guidelines = null
        ),
    )

    Column(
        modifier = Modifier.fillMaxHeight().widthIn(max = 1000.dp)
    ) {
        SettingsOption(
            text = Res.string.log_out,
            action = { authViewModel.onCommand(AuthCommand.Logout) }
        )
        SettingsOption(
            text = Res.string.change_profile_picture,
            imageVector = Icons.Default.Image,
            action = {
                scope.launch {
                    val takenImage = FileKit.openFilePicker(
                        type = FileKitType.Image,
                        mode = FileKitMode.Single
                    )
                    
                    if (takenImage != null) {
                        when (val result = imageCropper.crop(takenImage.toImageSrc())) {
                            CropError.LoadingError -> {}
                            CropError.SavingError -> {}
                            CropResult.Cancelled -> {}
                            is CropResult.Success -> {
                                settingsViewModel.onCommand(SettingsCommand.ImageChanged(result.bitmap.encodeToByteArray()))
                                showDialog = true
                            }
                        }
                    } else {
                        SnackbarController.onEvent(getString(Res.string.no_image_provided_error))
                    }
                }
            }
        )
        SettingsOption(
            text = Res.string.account_details_change,
            imageVector = Icons.Default.Person,
            action = { navigateToChangeAccountDetailsScreen() }
        )
        
        SettingsOption(
            text = Res.string.location_details_change,
            imageVector = Icons.Default.LocationOn,
            action = { navigateToChangeLocationDetailsScreen() }
        )
    }
}
