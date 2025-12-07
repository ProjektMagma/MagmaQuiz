package com.github.projektmagma.magmaquiz

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform