package com.github.projektmagma.magmaquiz.app.core.util

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

// Jeżeli piszesz to po pół sekundy wykona funkcję
suspend inline fun withSearchDelay(applyDelay: Boolean, delayInMillis: Long = 500, body: () -> Unit) {
    if (applyDelay) delay(delayInMillis)
    body()
}

@Suppress("SimpleDateFormat")
fun convertLongSecondsToString(time: Long, pattern: String = "dd.MM.yyyy HH:mm"): String {
    val date = Date(time * 1000) // Serwer operuje w timestampie sekundowym więc trzeba przemnożyć przez milisekundy 
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}