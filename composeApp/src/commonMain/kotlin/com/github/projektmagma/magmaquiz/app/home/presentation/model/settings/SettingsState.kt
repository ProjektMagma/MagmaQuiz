package com.github.projektmagma.magmaquiz.app.home.presentation.model.settings

import com.github.projektmagma.magmaquiz.shared.data.domain.Quiz


data class SettingsState(
    val profilePicture: ByteArray? = null,
    val quizzes: List<Quiz> = emptyList()
)
