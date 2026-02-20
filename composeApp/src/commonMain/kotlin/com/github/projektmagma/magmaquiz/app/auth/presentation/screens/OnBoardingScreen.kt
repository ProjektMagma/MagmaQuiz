package com.github.projektmagma.magmaquiz.app.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.app_icon
import magmaquiz.composeapp.generated.resources.app_name
import magmaquiz.composeapp.generated.resources.change_server
import magmaquiz.composeapp.generated.resources.log_in
import magmaquiz.composeapp.generated.resources.register
import magmaquiz.composeapp.generated.resources.welcome_in
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnBoardingScreen(
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToServerConfig: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.welcome_in),
            style = MaterialTheme.typography.headlineMedium
        )

        Image(
            modifier = Modifier.clip(MaterialTheme.shapes.large),
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = "AppIcon",
        )

        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = { navigateToLogin() }
        ) {
            Text(text = stringResource(Res.string.log_in))
        }

        Button(
            onClick = { navigateToRegister() }
        ) {
            Text(text = stringResource(Res.string.register))
        }

        Button(
            onClick = { navigateToServerConfig() }
        ) {
            Text(text = stringResource(Res.string.change_server))
        }
    }
}
