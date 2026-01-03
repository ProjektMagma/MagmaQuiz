package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError

@Composable
fun UsernameTextField(
    usernameText: String,
    usernameError: UsernameError?,
    onValueChange: (String) -> Unit 
){
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
        onValueChange = { onValueChange(it) }
    )
}