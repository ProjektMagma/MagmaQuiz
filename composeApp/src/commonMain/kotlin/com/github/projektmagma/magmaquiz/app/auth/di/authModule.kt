package com.github.projektmagma.magmaquiz.app.auth.di

import com.github.projektmagma.magmaquiz.app.auth.data.AuthService
import com.github.projektmagma.magmaquiz.app.auth.data.UserRepository
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthService)
    singleOf(::UserRepository)
    viewModelOf(::AuthViewModel)
}