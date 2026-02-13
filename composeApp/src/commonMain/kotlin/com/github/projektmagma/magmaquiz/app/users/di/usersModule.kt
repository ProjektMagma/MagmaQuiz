package com.github.projektmagma.magmaquiz.app.users.di

import com.github.projektmagma.magmaquiz.app.users.data.repository.UsersRepository
import com.github.projektmagma.magmaquiz.app.users.data.service.UsersService
import com.github.projektmagma.magmaquiz.app.users.presentation.UserDetailsViewModel
import com.github.projektmagma.magmaquiz.app.users.presentation.UsersViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val usersModule = module {
    singleOf(::UsersService)
    singleOf(::UsersRepository)
    viewModelOf(::UsersViewModel)
    viewModelOf(::UserDetailsViewModel)
}