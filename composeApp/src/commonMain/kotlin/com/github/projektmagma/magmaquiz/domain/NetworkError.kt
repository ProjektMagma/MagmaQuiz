package com.github.projektmagma.magmaquiz.domain

import com.github.projektmagma.magmaquiz.data.domain.abstraction.Error

enum class NetworkError: Error {
    NETWORK,
    CONFLICT,
    SERIALIZATION,
    MULTIPLE_CHOICES,
    NOT_FOUND,
    UNAUTHORIZED,
    UNKNOWN
}