package com.github.projektmagma.magmaquiz.data.networking

import com.github.projektmagma.magmaquiz.data.domain.Resource
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.lang.Exception

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