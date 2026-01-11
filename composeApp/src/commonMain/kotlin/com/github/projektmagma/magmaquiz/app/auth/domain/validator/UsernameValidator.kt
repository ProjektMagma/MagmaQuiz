package com.github.projektmagma.magmaquiz.app.auth.domain.validator

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError.FORBIDDEN_CHAR
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError.IS_EMPTY
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError.TOO_LONG
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.field_cannot_empty
import magmaquiz.composeapp.generated.resources.forbidden_char
import magmaquiz.composeapp.generated.resources.too_long_username
import org.jetbrains.compose.resources.StringResource

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

fun UsernameError.toResId(): StringResource{
    return when(this) {
        IS_EMPTY -> Res.string.field_cannot_empty
        TOO_LONG -> Res.string.too_long_username
        FORBIDDEN_CHAR -> Res.string.forbidden_char
    }
}