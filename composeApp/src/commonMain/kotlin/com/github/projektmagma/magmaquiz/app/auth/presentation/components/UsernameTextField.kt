package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError

@Composable
fun UsernameTextField(
    usernameText: String,
    usernameError: UsernameError?,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = usernameText,
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(
                text = "Username",
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Username"
            )
        },
        singleLine = true,
        isError = usernameError != null,
        supportingText = {
            Text(
                text = usernameError?.name ?: "",
                color = MaterialTheme.colorScheme.error,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}