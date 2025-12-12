package com.github.projektmagma.magmaquiz.domain

enum class NetworkError: Error {
    NETWORK,
    CONFLICT,
    SERIALIZATION,
    MULTIPLE_CHOICES,
    NOT_FOUND,
    UNAUTHORIZED,
    UNKNOWN
}