package com.github.projektmagma.magmaquiz.data.domain

sealed interface Resource<out D, out E> {
    data class Success<out D>(val data: D) : Resource<D, Nothing>
    data class Error<out E>(val error: E) : Resource<Nothing, E>
}
