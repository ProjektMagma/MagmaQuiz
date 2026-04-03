package com.github.projektmagma.magmaquiz.server.mailer

sealed class MailTemplates(val fileName: String) {
    data object AccountCreated : MailTemplates("account_created.html")
    data object VerificationCode : MailTemplates("verification_code.html")
    data object EmailChanged : MailTemplates("password_changed.html")
    data object PasswordChanged : MailTemplates("password_changed.html")
    data object AccountDeleted : MailTemplates("account_deleted.html")
}