package com.github.projektmagma.magmaquiz.app.home.di

import com.github.projektmagma.magmaquiz.app.home.presentation.HomeViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.RoomListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::RoomListViewModel)
}