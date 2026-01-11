package com.github.projektmagma.magmaquiz.app.core.util

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.readBytes
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

suspend fun PlatformFile?.compressImage(quality: Int): ByteArray? {
    val bytes = this?.readBytes()
    return if (bytes != null) {
        FileKit.compressImage(
            bytes = bytes,
            quality = quality,
            maxWidth = 1024,
            maxHeight = 1024,
            imageFormat = ImageFormat.PNG
        )
    } else null
}