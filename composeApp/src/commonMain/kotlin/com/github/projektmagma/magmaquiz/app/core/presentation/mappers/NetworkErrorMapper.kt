package com.github.projektmagma.magmaquiz.app.core.presentation.mappers

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.CONFLICT
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.MULTIPLE_CHOICES
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.NETWORK
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.NOT_FOUND
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.SERIALIZATION
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.SERVER_ERROR
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.UNAUTHORIZED
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError.UNKNOWN
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.conflict_error
import magmaquiz.composeapp.generated.resources.multiple_choices_error
import magmaquiz.composeapp.generated.resources.network_error
import magmaquiz.composeapp.generated.resources.not_found_error
import magmaquiz.composeapp.generated.resources.serialization_error
import magmaquiz.composeapp.generated.resources.server_error
import magmaquiz.composeapp.generated.resources.unathorized_error
import magmaquiz.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.StringResource

fun NetworkError.toResId(): StringResource{
    return when (this) {
        NETWORK -> Res.string.network_error
        CONFLICT -> Res.string.conflict_error
        SERIALIZATION -> Res.string.serialization_error
        MULTIPLE_CHOICES -> Res.string.multiple_choices_error
        NOT_FOUND -> Res.string.not_found_error
        UNAUTHORIZED -> Res.string.unathorized_error
        SERVER_ERROR -> Res.string.server_error
        UNKNOWN -> Res.string.unknown_error
    }
}