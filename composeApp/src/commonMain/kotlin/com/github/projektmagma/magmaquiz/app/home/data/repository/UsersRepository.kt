package com.github.projektmagma.magmaquiz.app.home.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.home.data.service.UsersService
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class UsersRepository(
    private val usersService: UsersService
) {

    private val _userList = MutableStateFlow<List<ForeignUser>>(emptyList())
    val userList = _userList.asStateFlow()

    private val _selectedUserData = MutableStateFlow<ForeignUser?>(null)
    private val _friendList = MutableStateFlow<List<ForeignUser>>(emptyList())
    val friendList = _friendList.asStateFlow()

    suspend fun getFindUsersByName(name: String): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getFindUsersByName(name).whenSuccess { _userList.value = it.data }
    }

    suspend fun getUserDataById(uuid: UUID): Resource<ForeignUser, NetworkError> {
        return usersService.getUserDataById(uuid).whenSuccess { _selectedUserData.value = it.data }
    }

    suspend fun getFriendList(): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getFriendList().whenSuccess { _friendList.value = it.data }
    }

    suspend fun getSendFriendInvite(uuid: UUID): Boolean {
        var wasSend = false
        usersService.getSendFriendInvite(uuid)
            .whenSuccess { wasSend = true }
        return wasSend
    }

    suspend fun getAcceptFriendInvite(uuid: UUID): Boolean {
        var wasAccepted = false
        usersService.getAcceptFriendInvite(uuid)
            .whenSuccess { wasAccepted = true }
        return wasAccepted
    }
}