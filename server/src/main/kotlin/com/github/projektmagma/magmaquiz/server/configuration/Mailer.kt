package com.github.projektmagma.magmaquiz.server.configuration

import com.github.projektmagma.magmaquiz.server.mailer.MailerService
import io.ktor.server.application.*

fun Application.configureMailer() {
    MailerService.populate(environment)
}