package com.github.projektmagma.magmaquiz.app.core.data.networking

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.util.network.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.net.ConnectException
import java.net.UnknownHostException

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Resource<T, NetworkError> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        Napier.e(e.message ?: "", e, "NetworkError")
        return Resource.Error(NetworkError.NETWORK)
    } catch (e: ClientRequestException) {
        Napier.e(e.message, e, "NetworkError")
        return Resource.Error(NetworkError.NO_INTERNET)
    } catch (e: ServerResponseException) {
        Napier.e(e.message, e, "NetworkError")
        return Resource.Error(NetworkError.UNKNOWN_HOST)
    } catch (e: ConnectException) {
        Napier.e(e.message ?: "", e, "NetworkError")
        return Resource.Error(NetworkError.NO_INTERNET)
    } catch (e: UnknownHostException) {
        Napier.e(e.message ?: "", e, "NetworkError")
        return Resource.Error(NetworkError.UNKNOWN_HOST)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Napier.e(e.message ?: "", e, "NetworkError")
        return Resource.Error(NetworkError.UNKNOWN)
    }
    
    return responseToResult(response)
}