package com.github.projektmagma.magmaquiz.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
        Text(text = "Logowanie")

        Button(
            onClick = { navigateToLogin() }
        ) {
            Text(text = "Loguj")
        }

        Button(
            onClick = { navigateToRegister() }
        ) {
            Text(text = "Rejestruj")
        }

        Button(
            onClick = { navigateToServerConfig() }
        ) {
            Text(text = "Serweruj")
        }
    }
}
