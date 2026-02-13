package com.github.projektmagma.magmaquiz.app.users.data.repository

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.app.users.data.service.UsersService
import com.github.projektmagma.magmaquiz.shared.data.domain.ForeignUser
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.whenSuccess
import java.util.*

class UsersRepository(
    private val usersService: UsersService
) { 
    suspend fun getFindUsersByName(name: String): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getFindUsersByName(name)
    }

    suspend fun getUserDataById(uuid: UUID): Resource<ForeignUser, NetworkError> {
        return usersService.getUserDataById(uuid)
    }

    suspend fun getFriendList(): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getFriendList()
    }
    
    suspend fun getIncomingInvitations(): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getIncomingInvitations()
    }
    
    suspend fun getOutgoingInvitations(): Resource<List<ForeignUser>, NetworkError> {
        return usersService.getOutgoingInvitations()
    }
    
    suspend fun getSendFriendInvite(uuid: UUID): Resource<Unit, NetworkError> {
        return usersService.getSendFriendInvite(uuid)
    }
    
    suspend fun getAcceptFriendInvite(uuid: UUID): Boolean {
        var wasAccepted = false
        usersService.getAcceptFriendInvite(uuid)
            .whenSuccess { wasAccepted = true }
        return wasAccepted
    }
}