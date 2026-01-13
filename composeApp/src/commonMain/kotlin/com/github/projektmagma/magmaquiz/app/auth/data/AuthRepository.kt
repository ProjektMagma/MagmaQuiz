package com.github.projektmagma.magmaquiz.app.auth.data

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.ThisUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val authService: AuthService
) {

    private val _thisUser = MutableStateFlow<ThisUser?>(null)
    val thisUser = _thisUser.asStateFlow()

    suspend fun registerUser(username: String, email: String, password: String): Resource<ThisUser, NetworkError> {
        return authService.registerUser(username, email, password).whenSuccess { _thisUser.value = it.data }
    }


    suspend fun loginUser(email: String, password: String): Resource<ThisUser, NetworkError> {
        return authService.loginUser(email, password).whenSuccess { _thisUser.value = it.data }

    }

    suspend fun whoAmI(): Resource<ThisUser, NetworkError> {
        return authService.whoAmI().whenSuccess { _thisUser.value = it.data }
    }

    suspend fun logoutUser(): Resource<Unit, NetworkError> {
        return authService.logoutUser().whenError { _thisUser.value = null }
    }
}