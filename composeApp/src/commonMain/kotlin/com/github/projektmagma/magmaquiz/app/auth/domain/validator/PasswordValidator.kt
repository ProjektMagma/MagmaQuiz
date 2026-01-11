package com.github.projektmagma.magmaquiz.app.auth.domain.validator

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError.IS_EMPTY
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError.NO_SPECIAL
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError.NO_UPPER
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError.TOO_SHORT
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.field_cannot_empty
import magmaquiz.composeapp.generated.resources.no_special_password
import magmaquiz.composeapp.generated.resources.no_uppercase_password
import magmaquiz.composeapp.generated.resources.too_short_password
import org.jetbrains.compose.resources.StringResource

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

fun PasswordError.toResId(): StringResource{
    return when (this) {
        IS_EMPTY -> Res.string.field_cannot_empty
        TOO_SHORT -> Res.string.too_short_password
        NO_UPPER -> Res.string.no_uppercase_password
        NO_SPECIAL -> Res.string.no_special_password
    }
}