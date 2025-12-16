package com.github.projektmagma.magmaquiz.data.domain.abstraction

typealias RootError = Error

sealed interface Resource<out D, out E: RootError> {
    data class Success<out D>(val data: D) : Resource<D, Nothing>
    data class Error<out E: RootError>(val error: E) : Resource<Nothing, E>
}
