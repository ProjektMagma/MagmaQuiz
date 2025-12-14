package com.github.projektmagma.magmaquiz.data.domain.abstraction

import io.ktor.http.*

sealed interface NetworkResource<out D> {
    data class Success<out D>(val data: D, val statusCode: HttpStatusCode = HttpStatusCode.OK) : NetworkResource<D>
    data class Error(val statusCode: HttpStatusCode, val errorDescription: String = statusCode.description) :
        NetworkResource<Nothing>
}

