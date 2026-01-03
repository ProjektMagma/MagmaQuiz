package com.github.projektmagma.magmaquiz.shared.data.domain.abstraction

typealias RootError = Error

//@Suppress("UNCHECKED_CAST")
sealed interface Resource<out D, out E : RootError> {
    data class Success<out D>(val data: D) : Resource<D, Nothing>
    data class Error<out E : RootError>(val error: E) : Resource<Nothing, E>
}

inline fun <D, E : RootError> Resource<D, E>.whenSuccess(block: (Resource.Success<D>) -> Unit): Resource<D, E> {
    if (this is Resource.Success) block(this)
    return this
}

inline fun <D, E : RootError> Resource<D, E>.whenError(block: (Resource.Error<E>) -> Unit): Resource<D, E> {
    if (this is Resource.Error) block(this)
    return this
}