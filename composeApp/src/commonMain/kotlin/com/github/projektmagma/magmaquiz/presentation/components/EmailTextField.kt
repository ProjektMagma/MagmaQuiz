package com.github.projektmagma.magmaquiz.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.projektmagma.magmaquiz.domain.validator.EmailError

@Composable
fun EmailTextField(
    emailText: String,
    emailError: EmailError?,
    onValueChange: (String) -> Unit
){
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = emailText,
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(
                text = "Email",
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
        isError = emailError != null,
        supportingText = {
            Text(
                text = emailError?.name ?: "",
                color = MaterialTheme.colorScheme.error,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
        onValueChange = { onValueChange(it) }
    )
}