package com.github.projektmagma.magmaquiz.app.settings.presentation.model.account

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

data class AccountDetailsChangeState(
    val bio: String = "",
    val username: String = "",
    val usernameError: Error? = null
)
