package com.github.projektmagma.magmaquiz.app.home.data.service

import com.github.projektmagma.magmaquiz.app.core.data.networking.safeCall
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.util.UUID

class UsersService(
    private val httpClient: HttpClient,
) {
    suspend fun getFindUsersByName(name: String): Resource<List<ForeignUser>, NetworkError> {
        return safeCall<List<ForeignUser>> {
            httpClient.get("users/find/$name")
        }
    }

    suspend fun getUserDataById(uuid: UUID): Resource<ForeignUser, NetworkError> {
        return safeCall<ForeignUser> {
            httpClient.get("users/userData/$uuid")
        }
    }

    suspend fun getFriendList(): Resource<List<ForeignUser>, NetworkError> {
        return safeCall<List<ForeignUser>> {
            httpClient.get("users/friendship/friendList")
        }
    }

    suspend fun getSendFriendInvite(uuid: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("users/friendship/sendInvitation/$uuid")
        }
    }

    suspend fun getAcceptFriendInvite(uuid: UUID): Resource<Unit, NetworkError> {
        return safeCall<Unit> {
            httpClient.get("users/friendship/acceptInvitation/$uuid")
        }
    }
}