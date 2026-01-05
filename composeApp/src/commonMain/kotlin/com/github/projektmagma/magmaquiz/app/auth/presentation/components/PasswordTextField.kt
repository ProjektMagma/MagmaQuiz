package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.PasswordError

@Composable
fun PasswordTextField(
    passwordText: String,
    passwordError: PasswordError?,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,

    ) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = passwordText,
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Padlock"
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { isVisible = !isVisible }
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = "Visibility"
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = passwordError != null,
        supportingText = {
            Text(
                text = passwordError?.name ?: "",
                color = MaterialTheme.colorScheme.error,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}