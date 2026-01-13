package com.github.projektmagma.magmaquiz.app.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.auth.presentation.model.auth.AuthCommand
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.NetworkEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.home.presentation.SettingsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.model.UiEvent
import com.github.projektmagma.magmaquiz.app.home.presentation.model.settings.SettingsCommand
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(navigateToAuth: () -> Unit) {

    val authViewModel = koinViewModel<AuthViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val scope = rememberCoroutineScope()
    var image by remember { mutableStateOf<ByteArray?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isFileImageSelectorOpen by remember { mutableStateOf(false) }

    LaunchedEffect(authViewModel.authChannel) {
        authViewModel.authChannel.collect { event ->
            when (event) {
                is NetworkEvent.Failure -> {
                    SnackbarController.onEvent(event.networkError.name)
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
                        .height(256.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .crossfade(true)
                        .data(image)
                        .build(),
                    contentScale = ContentScale.Fit,
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Button(onClick = {
                authViewModel.onCommand(AuthCommand.Logout)
            }) {
                Text(text = stringResource(Res.string.log_out))
            }

            Button(onClick = {
                if (!isFileImageSelectorOpen)
                    scope.launch {
                        isFileImageSelectorOpen = true
                        val takenImage = FileKit.openFilePicker(
                            type = FileKitType.Image,
                            mode = FileKitMode.Single
                        )
                        if (takenImage != null) {
                            image = FileKit.compressImage(
                                bytes = takenImage.readBytes(),
                                quality = 75,
                                maxWidth = 512,
                                maxHeight = 512,
                                imageFormat = ImageFormat.JPEG
                            )
                            settingsViewModel.onCommand(SettingsCommand.ImageChanged(image))
                            showDialog = true
                        } else SnackbarController.onEvent(getString(Res.string.no_image_provided_error))
                        isFileImageSelectorOpen = false
                    }
            }) {
                Text(text = stringResource(Res.string.change_profile_picture))
            }
        }
    }
}