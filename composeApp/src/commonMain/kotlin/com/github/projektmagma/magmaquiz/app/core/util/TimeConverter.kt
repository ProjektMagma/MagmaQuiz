package com.github.projektmagma.magmaquiz.app.core.util

object TimeConverter {
    fun Long.toSeconds(): Int = (this / 1000).toInt()
    fun Int.toMillis(): Long = (this * 1000).toLong()
}