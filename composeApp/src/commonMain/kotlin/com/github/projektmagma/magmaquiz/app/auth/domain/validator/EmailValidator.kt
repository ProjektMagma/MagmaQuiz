package com.github.projektmagma.magmaquiz.app.auth.domain.validator

import com.github.projektmagma.magmaquiz.app.auth.domain.validator.EmailError.INVALID
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.EmailError.IS_EMPTY
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.field_cannot_empty
import magmaquiz.composeapp.generated.resources.invalid_email
import org.jetbrains.compose.resources.StringResource

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

fun EmailError.toResId(): StringResource {
    return when(this) {
        IS_EMPTY -> Res.string.field_cannot_empty
        INVALID -> Res.string.invalid_email
    }
}