package com.github.projektmagma.magmaquiz.app.core.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarController {
    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()
    
    suspend fun onEvent(message: String){
        _events.send(message)
    }
}