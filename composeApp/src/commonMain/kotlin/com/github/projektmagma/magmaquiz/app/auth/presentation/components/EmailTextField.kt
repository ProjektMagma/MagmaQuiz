package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.EmailError
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.toResId
import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Error
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.email
import magmaquiz.composeapp.generated.resources.email_taken
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailTextField(
    emailText: String,
    error: Error?,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {
    val errorMessage = when (error){
        is NetworkError -> Res.string.email_taken
        is EmailError -> error.toResId()
        else -> null
    }
    
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = emailText,
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(
                text = stringResource(Res.string.email),
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email"
            )
        },
        singleLine = true,
        isError = errorMessage != null,
        supportingText = {
            Text(
                text = if (errorMessage != null) stringResource(errorMessage) else "",
                color = MaterialTheme.colorScheme.error,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}