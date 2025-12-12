package com.github.projektmagma.magmaquiz.data

import com.github.projektmagma.magmaquiz.data.domain.Resource
import com.github.projektmagma.magmaquiz.data.domain.User
import com.github.projektmagma.magmaquiz.data.networking.safeCall
import com.github.projektmagma.magmaquiz.domain.NetworkError
import io.ktor.client.HttpClient

class UserRepository(
    private val httpClient: HttpClient
) {
    suspend fun registerUser() : Resource<User, NetworkError>{
//        safeCall<User> { 
//            
//        }
    }
    
    suspend fun loginUser() : Resource<User, NetworkError> {
//        safeCall<User> { 
//            
//        }
    }
}