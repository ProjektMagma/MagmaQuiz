package com.github.projektmagma.magmaquiz.domain.validator

import com.github.projektmagma.magmaquiz.data.domain.abstraction.Error

fun validatePassword(password: String): PasswordError? {
    if (password.isEmpty()){
        return PasswordError.IS_EMPTY
    }
    if (password.length < 6){
        return PasswordError.TOO_SHORT
    }
    if (password.none {it.isUpperCase()}){
        return PasswordError.NO_UPPER
    }
    
    val regex = Regex("[!@#$%&^*]+")
    if (!password.contains(regex)){
        return PasswordError.NO_SPECIAL
    }
    return null
}

fun validateIsEmptyPassword(password: String): PasswordError? {
    return PasswordError.IS_EMPTY.takeIf { password.isEmpty() }
}

enum class PasswordError: Error {
    IS_EMPTY,
    TOO_SHORT,
    NO_UPPER,
    NO_SPECIAL,
}