package com.github.projektmagma.magmaquiz.app.home.data

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class UsersService(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
    private val apiDataStore: ApiDataStore
) {
    suspend fun getFindUsersByName(name: String): Resource<List<ForeignUser>, NetworkError> {
        return safeCall<List<ForeignUser>> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/users/find/$name") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getUserDataById(uuid: UUID): Resource<ForeignUser, NetworkError> {
        return safeCall<ForeignUser> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/users/userData/$uuid") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getFriendList(): Resource<List<ForeignUser>, NetworkError> {
        return safeCall<List<ForeignUser>> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/users/friendship/friendList") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getSendFriendInvite(uuid: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/users/friendship/sendInvitation/$uuid") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }

    suspend fun getAcceptFriendInvite(uuid: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("${baseUrlProvider.getBaseUrl()}/users/friendship/acceptInvitation/$uuid") {
                contentType(ContentType.Application.Json)
                header("user_session", apiDataStore.getSessionHeader())
            }
        }
    }
}