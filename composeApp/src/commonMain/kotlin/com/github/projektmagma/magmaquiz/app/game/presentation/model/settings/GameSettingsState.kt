package com.github.projektmagma.magmaquiz.app.game.presentation.model.settings

data class GameSettingsState(
    val roomName: String = "",
    val questionTime: Int = 15,
    val isPublic: Boolean = true
)