package com.github.projektmagma.magmaquiz.app.game.presentation.model.settings

sealed interface GameSettingsCommand {
    data class NameChanged(val newName: String) : GameSettingsCommand
    data class QuestionTimeChanged(val newTime: Int) : GameSettingsCommand
    data object Submit : GameSettingsCommand
}