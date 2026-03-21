package com.github.projektmagma.magmaquiz.app.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.projektmagma.magmaquiz.app.auth.data.AuthRepository
import com.github.projektmagma.magmaquiz.app.core.presentation.model.events.LocalEvent
import com.github.projektmagma.magmaquiz.app.settings.data.repository.CountriesRepository
import com.github.projektmagma.magmaquiz.app.settings.data.repository.SettingsRepository
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.country.CountryChangeState
import com.github.projektmagma.magmaquiz.app.settings.presentation.model.country.LocationDetailsChangeCommand
import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationDetailsChangeViewModel(
    private val countriesRepository: CountriesRepository,
    private val settingsRepository: SettingsRepository,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(CountryChangeState(
        userTown = authRepository.thisUser.value?.userTown ?: "",
    ))
    val state = _state.asStateFlow()
    
    private val _event = Channel<LocalEvent>()
    val event = _event.receiveAsFlow()
    
    init {
        viewModelScope.launch {
            val country = countriesRepository.getCountryByCode(authRepository.thisUser.value?.userCountryCode ?: "")
            _state.update { it.copy(
                countriesList = countriesRepository.getAllCountries(),
                selectedCountry = country
            ) }
        }
    }
    
    fun onCommand(command: LocationDetailsChangeCommand){
        when (command) {
           is LocationDetailsChangeCommand.CountryNameChanged ->{
               _state.update { it.copy(countryName = command.newName) }
               getCountriesByName()
           }
           is LocationDetailsChangeCommand.CountrySelected -> _state.update { it.copy(
               selectedCountry = command.country,
               countryName = "",
               filteredCountriesList = emptyList()
           ) }
            
            is LocationDetailsChangeCommand.UserTownChanged -> _state.update { it.copy(userTown = command.newTown) }
            LocationDetailsChangeCommand.Save -> saveDetails()
        }
    }
    
    private fun getCountriesByName(){
        viewModelScope.launch { 
            val countries = _state.value.countriesList.filter { 
                it.name.contains(_state.value.countryName, true) 
            }
            _state.update { it.copy(filteredCountriesList = countries) }
        }
    }

    
    private fun saveDetails(){
        viewModelScope.launch { 
            val town = _state.value.userTown
            val countryCode = _state.value.selectedCountry!!.code
            
            val ifSuccess = listOf(
                settingsRepository.changeTown(town),
                settingsRepository.changeCountryCode(countryCode)
            ).all { it is Resource.Success }
            
            if (ifSuccess) {
                authRepository.thisUser.update { 
                    it?.copy(
                        userTown = town, 
                        userCountryCode = countryCode
                    ) 
                }
                usersRepository.user.value = authRepository.thisUser.value
                _event.send(LocalEvent.Success)
            } else {
                _event.send(LocalEvent.Failure)
            }
        }
    }
}