package com.github.projektmagma.magmaquiz.app.core.di

import androidx.compose.runtime.mutableStateListOf
import com.github.projektmagma.magmaquiz.app.core.presentation.navigation.Route

class Navigator(startDestination: Route) {
    val backstack = mutableStateListOf(startDestination)
    
    fun goTo(destination: Route) {
        backstack.add(destination)
    }
    
    fun clearAndGoTo(destination: Route){
        backstack.clear()
        backstack.add(destination)
    }
    
    fun goBack(){
        backstack.removeLastOrNull()
    }
    
    fun checkAndNavigate(destination: Route){
        val prevRoute = backstack.getOrNull(backstack.size - 2) ?: false
        if (prevRoute == destination) {
            goBack()
        } else {
            goTo(destination)
        }
    }
}