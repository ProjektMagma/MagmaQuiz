package com.github.projektmagma.magmaquiz.app.core.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

enum class NetworkError : Error {
    NETWORK,
    NO_INTERNET,
    UNKNOWN_HOST,
    CONFLICT,
    SERIALIZATION,
    MULTIPLE_CHOICES,
    NOT_FOUND,
    UNAUTHORIZED,
    SERVER_ERROR,
    UNKNOWN
}