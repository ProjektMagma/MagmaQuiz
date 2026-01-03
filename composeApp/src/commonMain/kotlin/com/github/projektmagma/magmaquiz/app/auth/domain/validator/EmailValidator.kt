package com.github.projektmagma.magmaquiz.app.auth.domain.validator

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error

fun validateEmail(email: String): EmailError? {
    if (email.isEmpty()){
        return EmailError.IS_EMPTY 
    }
    
    val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    if (!email.matches(regex)){
        return EmailError.INVALID
    }
    return null
}

enum class EmailError: Error {
    IS_EMPTY,
    INVALID
}