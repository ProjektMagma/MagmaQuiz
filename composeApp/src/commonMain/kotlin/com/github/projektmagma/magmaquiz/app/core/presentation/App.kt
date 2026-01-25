package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.compose.animation.animateBounds
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.NavRoot
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import io.github.vinceglb.filekit.coil.addPlatformFileSupport

@OptIn(InternalComposeUiApi::class)
@Composable
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
fun App(modifier: Modifier = Modifier) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .build()
    }
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(SnackbarController.events) {
            SnackbarController.events.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    LookaheadScope {
        Scaffold(
            modifier = modifier
                .animateBounds(this),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        ) { _ ->
            NavRoot()
        }
        }
}
