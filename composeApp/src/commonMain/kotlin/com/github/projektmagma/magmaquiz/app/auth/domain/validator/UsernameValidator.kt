package com.github.projektmagma.magmaquiz.app.auth.domain.validator

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

fun validateUsername(username: String): UsernameError? {
    if (username.isEmpty()){
        return UsernameError.IS_EMPTY
    }
    
    val regex = Regex("[!@#$%&^*]+")
    if (username.matches(regex)){
        return UsernameError.FORBIDDEN_CHAR
    }
    
    if (username.length > 20){
        return UsernameError.TOO_LONG
    }
    return null
}

enum class UsernameError: Error {
    IS_EMPTY,
    TOO_LONG,
    FORBIDDEN_CHAR
}