package com.github.projektmagma.magmaquiz.app.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.projektmagma.magmaquiz.BuildKonfig
import magmaquiz.composeapp.generated.resources.*
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
        Spacer(modifier = Modifier.height(64.dp))
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


        Column(
            modifier = Modifier.fillMaxHeight()
                .padding(bottom = 4.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(Res.string.version)}: ${BuildKonfig.version}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${Typography.copyright} 2026 Projekt Magma.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

    }
}
