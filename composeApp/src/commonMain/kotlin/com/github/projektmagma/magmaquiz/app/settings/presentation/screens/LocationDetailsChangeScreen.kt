package com.github.projektmagma.magmaquiz.app.settings.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.core.util.SnackbarController
import com.github.projektmagma.magmaquiz.app.settings.domain.model.Country
import com.github.projektmagma.magmaquiz.app.settings.presentation.LocationDetailsChangeViewModel
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.location.LocationDetailsChangeCommand
import magmaquiz.composeapp.generated.resources.Res
import magmaquiz.composeapp.generated.resources.cant_save
import magmaquiz.composeapp.generated.resources.country
import magmaquiz.composeapp.generated.resources.no_country_selected
import magmaquiz.composeapp.generated.resources.save
import magmaquiz.composeapp.generated.resources.selected_country
import magmaquiz.composeapp.generated.resources.set_location
import magmaquiz.composeapp.generated.resources.town
import magmaquiz.composeapp.generated.resources.your_location
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailsChangeScreen(
    locationDetailsChangeViewModel: LocationDetailsChangeViewModel = koinViewModel(),
    navigateBack: () -> Unit
) {
    val state by locationDetailsChangeViewModel.state.collectAsStateWithLifecycle()
    var expanded by rememberSaveable { mutableStateOf(false) }

    val canSave = state.userTown.isNotBlank() && state.selectedCountry != null

    LaunchedEffect(Unit){
        locationDetailsChangeViewModel.event.collect { 
            when (it) {
                LocalEvent.Failure -> {
                    SnackbarController.onEvent(getString(Res.string.cant_save))
                }
                LocalEvent.Success -> { navigateBack() }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .widthIn(max = 1000.dp)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(Res.string.your_location),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(Res.string.set_location),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.userTown,
                onValueChange = {
                    locationDetailsChangeViewModel.onCommand(
                        LocationDetailsChangeCommand.UserTownChanged(it)
                    )
                },
                singleLine = true,
                label = { Text(stringResource(Res.string.town)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                    value = state.countryName,
                    onValueChange = {
                        locationDetailsChangeViewModel.onCommand(
                            LocationDetailsChangeCommand.CountryNameChanged(it)
                        )
                        if (!expanded) expanded = true
                    },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.country)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )

                ExposedDropdownMenu(
                    expanded = expanded && state.filteredCountriesList.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    state.filteredCountriesList.forEach { country ->
                        DropdownMenuItem(
                            text = { CountryData(country = country) },
                            onClick = {
                                locationDetailsChangeViewModel.onCommand(
                                    LocationDetailsChangeCommand.CountrySelected(country)
                                )
                                expanded = false
                            }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.selected_country),
                        style = MaterialTheme.typography.titleSmall
                    )
                    state.selectedCountry?.let { selected ->
                        CountryData(country = selected)
                    } ?: Text(
                        text = stringResource(Res.string.no_country_selected),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave,
                onClick = {
                    locationDetailsChangeViewModel.onCommand(LocationDetailsChangeCommand.Save)
                }
            ) {
                Text(stringResource(Res.string.save))
            }
        }
    }
}

@Composable
fun CountryData(country: Country) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = country.flag,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = country.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "(${country.code})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
