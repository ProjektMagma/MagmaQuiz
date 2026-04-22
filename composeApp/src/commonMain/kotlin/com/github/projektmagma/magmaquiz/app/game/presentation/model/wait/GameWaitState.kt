package com.github.projektmagma.magmaquiz.app.game.presentation.model.wait

data class GameWaitState(
    val isVisibleDialog: Boolean = false,
    val errorMessage: String? = "",
    val isUserInitializeLeave: Boolean = false
)