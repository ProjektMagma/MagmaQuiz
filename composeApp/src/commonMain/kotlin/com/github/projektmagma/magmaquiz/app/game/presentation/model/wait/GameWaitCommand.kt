package com.github.projektmagma.magmaquiz.app.game.presentation.model.wait

sealed interface GameWaitCommand {
    data class DialogVisibilityChanged(val visibility: Boolean) : GameWaitCommand
}