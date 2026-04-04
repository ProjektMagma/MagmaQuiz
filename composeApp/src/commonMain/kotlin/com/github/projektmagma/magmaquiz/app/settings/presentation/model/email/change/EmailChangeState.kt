package com.github.projektmagma.magmaquiz.app.settings.presentation.model.email.change

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

data class EmailChangeState(
    val email: String = "",
    val emailError: Error? = null,
)