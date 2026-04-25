package com.github.projektmagma.magmaquiz.server.mailer

import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.simplejavamail.api.email.ContentTransferEncoding
import org.simplejavamail.api.email.Recipient
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import java.io.File

object MailerService {

    private lateinit var _mailer: Mailer
    private lateinit var _emailFrom: String

    private lateinit var _supportEmail: String
    private lateinit var _displayedName: String

    private lateinit var _emailTemplatesFolder: File
    private const val TIMEOUT_IN_MILLIS = 10 * 1000
    private const val THREAD_POOL_SIZE = 20


    fun populate(environment: ApplicationEnvironment) {
        _displayedName = environment.config.property("mailer.displayed_name").getString()
        _emailFrom = environment.config.property("mailer.email").getString()
        _supportEmail = environment.config.property("mailer.support_email").getString()

        val host = environment.config.property("mailer.host").getString()
        val port = environment.config.property("mailer.port").getString().toInt()
        val password = environment.config.property("mailer.password").getString()

        _mailer = MailerBuilder
            .withSMTPServer(host, port, _emailFrom, password)
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .withSessionTimeout(TIMEOUT_IN_MILLIS)
            .withDebugLogging(true)
            .withThreadPoolSize(THREAD_POOL_SIZE)
            .buildMailer()

        _emailTemplatesFolder = File("./email_templates/${environment.config.property("mailer.language").getString()}")

        if (!_emailTemplatesFolder.exists())
            _emailTemplatesFolder.mkdirs()
    }

    fun sendMail(emailTo: String, emailTemplate: MailTemplates, vararg replaceValues: Pair<String, String>) {
        val emailFile = File("${_emailTemplatesFolder.path}/${emailTemplate.fileName}")
        if (!emailFile.exists())
            throw IllegalStateException("Email Template ${emailTemplate.fileName} does not exist under ${_emailTemplatesFolder.path}.")

        val mailScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        mailScope.launch {

            val emailHtmlText = emailFile
                .readText(Charsets.UTF_8)
                .replaceValues(*replaceValues, Pair("support_email", _supportEmail))


            val email = EmailBuilder.startingBlank()
                .from(Recipient(_displayedName, _emailFrom, null))
                .to(emailTo)
                .withSubject(emailHtmlText.extractSubject())
                .withHTMLText(emailHtmlText)
                .withContentTransferEncoding(ContentTransferEncoding.BASE_64)
                .buildEmail()


            _mailer.sendMail(email)
        }
    }
}


private fun String.extractSubject(): String {
    return this.substring(this.indexOf("<title>"), this.indexOf("</title>")).replace("<title>", "")
}

private fun String.replaceValues(vararg replaceValues: Pair<String, String>): String {
    var result = this

    replaceValues.forEach {
        result = result.replace("[${it.first}]", it.second)
    }

    return result
}