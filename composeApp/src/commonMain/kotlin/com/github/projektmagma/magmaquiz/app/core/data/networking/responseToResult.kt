package com.github.projektmagma.magmaquiz.app.core.data.networking

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.github.aakira.napier.Napier
import io.ktor.client.call.*
import io.ktor.client.statement.*

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Resource<T, NetworkError>{
    return when(response.status.value){
        in 200..299 -> {
            try {
                Resource.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Napier.e(e.message, e, "NetworkError")
                Resource.Error(NetworkError.SERIALIZATION)
            }
        }
        300 -> Resource.Error(NetworkError.MULTIPLE_CHOICES)
        401 -> Resource.Error(NetworkError.UNAUTHORIZED)
        404 -> Resource.Error(NetworkError.NOT_FOUND)
        409 -> Resource.Error(NetworkError.CONFLICT)
        500 -> Resource.Error(NetworkError.SERVER_ERROR)
        else -> Resource.Error(NetworkError.UNKNOWN)
    }
}