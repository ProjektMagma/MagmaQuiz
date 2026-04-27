package com.github.projektmagma.magmaquiz.app.core.presentation.mappers

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.auth_conflict_error
import magmaquiz.composeapp.generated.resources.auth_multiple_choices_error
import magmaquiz.composeapp.generated.resources.auth_not_found_error
import magmaquiz.composeapp.generated.resources.auth_unauthorized_error
import magmaquiz.composeapp.generated.resources.conflict_error
import magmaquiz.composeapp.generated.resources.forbidden
import magmaquiz.composeapp.generated.resources.multiple_choices_error
import magmaquiz.composeapp.generated.resources.network_error
import magmaquiz.composeapp.generated.resources.no_internet_error
import magmaquiz.composeapp.generated.resources.not_found_error
import magmaquiz.composeapp.generated.resources.serialization_error
import magmaquiz.composeapp.generated.resources.server_error
import magmaquiz.composeapp.generated.resources.unauthorized_error
import magmaquiz.composeapp.generated.resources.unknown_error
import magmaquiz.composeapp.generated.resources.unknown_host_error
import org.jetbrains.compose.resources.StringResource

fun NetworkError.toResId(contextType: ErrorMessageContext = ErrorMessageContext.Server): StringResource {

    return when (this) {
        NetworkError.NETWORK -> Res.string.network_error
        NetworkError.NO_INTERNET -> Res.string.no_internet_error
        NetworkError.UNKNOWN_HOST -> Res.string.unknown_host_error
        NetworkError.SERIALIZATION -> Res.string.serialization_error
        NetworkError.SERVER_ERROR -> Res.string.server_error
        NetworkError.UNKNOWN -> Res.string.unknown_error

        else -> {
            when (contextType) {
                ErrorMessageContext.Auth -> when (this) {
                    NetworkError.CONFLICT -> Res.string.auth_conflict_error
                    NetworkError.MULTIPLE_CHOICES -> Res.string.auth_multiple_choices_error
                    NetworkError.NOT_FOUND -> Res.string.auth_not_found_error
                    NetworkError.UNAUTHORIZED -> Res.string.auth_unauthorized_error
                    NetworkError.FORBIDDEN -> Res.string.forbidden
                }

                ErrorMessageContext.Server -> when (this) {
                    NetworkError.CONFLICT -> Res.string.conflict_error
                    NetworkError.MULTIPLE_CHOICES -> Res.string.multiple_choices_error
                    NetworkError.NOT_FOUND -> Res.string.not_found_error
                    NetworkError.UNAUTHORIZED -> Res.string.unauthorized_error
                    NetworkError.FORBIDDEN -> Res.string.forbidden
                }
            }
        }
    }
}