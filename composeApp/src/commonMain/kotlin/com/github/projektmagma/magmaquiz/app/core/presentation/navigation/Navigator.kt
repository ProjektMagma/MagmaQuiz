package com.github.projektmagma.magmaquiz.app.core.presentation.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey){
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun currentBackStackSize(): Int{
        return state.backStacks[state.topLevelRoute]?.size ?: 0
    }

    fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("NI MA")

        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}