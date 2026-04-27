package com.github.projektmagma.magmaquiz.app.settings.presentation.model.password.verify

sealed interface PasswordVerifyEvent {
    data object CodeSent : PasswordVerifyEvent
    data object Verified : PasswordVerifyEvent
    data object FailureSent : PasswordVerifyEvent
    data object FailureVerify : PasswordVerifyEvent
}
