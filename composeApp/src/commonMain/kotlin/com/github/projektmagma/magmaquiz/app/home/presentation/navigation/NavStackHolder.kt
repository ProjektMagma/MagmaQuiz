package com.github.projektmagma.magmaquiz.app.home.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

object NavStackHolder {
    lateinit var mainBackStack: NavBackStack<NavKey>
    lateinit var quizzesBackstack: NavBackStack<NavKey>
    lateinit var usersNavBackStack: NavBackStack<NavKey>
    lateinit var settingsBackStack: NavBackStack<NavKey>
}