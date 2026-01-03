package com.github.projektmagma.magmaquiz.app.core.data.networking

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.ktor.client.statement.*
import io.ktor.util.network.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Resource<T, NetworkError> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        return Resource.Error(NetworkError.NETWORK)
    } catch (e: Exception){
        currentCoroutineContext().ensureActive()
        return Resource.Error(NetworkError.UNKNOWN)
    }
    
    return responseToResult(response)
}