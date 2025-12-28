package com.github.projektmagma.magmaquiz.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.domain.validator.UsernameError

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