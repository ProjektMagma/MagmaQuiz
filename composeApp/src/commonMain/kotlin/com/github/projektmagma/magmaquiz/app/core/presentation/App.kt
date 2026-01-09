package com.github.projektmagma.magmaquiz.app.core.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.NavRoot
import com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme.MagmaQuizTheme
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import io.github.vinceglb.filekit.coil.addPlatformFileSupport

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { 
                addPlatformFileSupport()
            }
            .build()
    }
    MagmaQuizTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        
        LaunchedEffect(SnackbarController.events){
            SnackbarController.events.collect { message -> 
                snackbarHostState.showSnackbar(message)        
            }
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        ) { innerPadding -> 
            NavRoot(Modifier.padding(innerPadding))
        }
    }
}