package com.github.projektmagma.magmaquiz.data.networking

import com.github.projektmagma.magmaquiz.data.domain.Resource
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Resource<T, NetworkError>{
    return when(response.status.value){
        in 200..299 -> {
            try {
                Resource.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Resource.Error(NetworkError.SERIALIZATION)
            }
        }
        300 -> Resource.Error(NetworkError.MULTIPLE_CHOICES)
        401 -> Resource.Error(NetworkError.UNAUTHORIZED)
        404 -> Resource.Error(NetworkError.NOT_FOUND)
        409 ->  Resource.Error(NetworkError.CONFLICT)
        else -> Resource.Error(NetworkError.UNKNOWN)
    }
}