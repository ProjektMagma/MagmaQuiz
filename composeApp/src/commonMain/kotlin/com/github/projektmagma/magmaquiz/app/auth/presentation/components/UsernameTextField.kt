package com.github.projektmagma.magmaquiz.app.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.UsernameError
import com.github.projektmagma.magmaquiz.app.auth.domain.validator.toResId
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

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
                text = stringResource(Res.string.username),
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
                text = if (usernameError != null) stringResource(usernameError.toResId()) else "",
                color = MaterialTheme.colorScheme.error,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}