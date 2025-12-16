package com.github.projektmagma.magmaquiz.domain.validator

import com.github.projektmagma.magmaquiz.data.domain.abstraction.Error

fun validateRepeatedPassword(password: String, repeatedPassword: String): RepeatedPasswordError? {
    if (repeatedPassword.isEmpty()){
        return RepeatedPasswordError.IS_EMPTY
    }
    if (!repeatedPassword.equals(password)){
        return RepeatedPasswordError.NOT_MATCH
    }
    return null
}

enum class RepeatedPasswordError : Error{
    IS_EMPTY,
    NOT_MATCH
}