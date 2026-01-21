package com.github.projektmagma.magmaquiz.app.core.presentation.mappers

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import magmaquiz.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

fun NetworkError.toResId(): StringResource{
    return when (this) {
        NetworkError.NETWORK -> Res.string.network_error
        NetworkError.NO_INTERNET -> Res.string.no_internet_error
        NetworkError.UNKNOWN_HOST -> Res.string.unknown_host_error
        NetworkError.CONFLICT -> Res.string.conflict_error
        NetworkError.SERIALIZATION -> Res.string.serialization_error
        NetworkError.MULTIPLE_CHOICES -> Res.string.multiple_choices_error
        NetworkError.NOT_FOUND -> Res.string.not_found_error
        NetworkError.UNAUTHORIZED -> Res.string.unauthorized_error
        NetworkError.SERVER_ERROR -> Res.string.server_error
        NetworkError.UNKNOWN -> Res.string.unknown_error
    }
}