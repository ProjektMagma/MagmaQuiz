package com.github.projektmagma.magmaquiz.app.core.domain

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

enum class NetworkError: Error {
    NETWORK,
    CONFLICT,
    SERIALIZATION,
    MULTIPLE_CHOICES,
    NOT_FOUND,
    UNAUTHORIZED,
    UNKNOWN
}