package com.github.projektmagma.magmaquiz.app.core.util

import kotlinx.coroutines.delay

// Jeżeli piszesz to po pół sekundy wykona funkcję
suspend inline fun withSearchDelay(applyDelay: Boolean, body: () -> Unit) {
    if (applyDelay) delay(500)
    body()
}