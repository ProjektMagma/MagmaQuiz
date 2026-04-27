package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.entry

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.EmailError

data class PasswordEmailEntryState(
    val email: String? = null,
    val emailError: EmailError? = null
)