package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey){
        when (route) {
            in state.backStacks.keys if state.topLevelRoute == route -> {
                currentBackStack().clear()
                currentBackStack().add(route)
            }
            in state.backStacks.keys -> {
                state.topLevelRoute = route
            }
            else -> {
                val existingStack = state.backStacks.entries.firstOrNull { (_, value) ->  
                    value.any { it == route }
                }
                if (existingStack != null) {
                    state.topLevelRoute = existingStack.key
                } else if (currentBackStack().last() != route) {
                    currentBackStack().add(route)
                }
            }
        }
    }
    
    fun currentBackStack(): NavBackStack<NavKey>{
        return state.backStacks[state.topLevelRoute] ?: error("NI MA")
    }

    fun goBack(){
        val currentStack = currentBackStack()

        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}