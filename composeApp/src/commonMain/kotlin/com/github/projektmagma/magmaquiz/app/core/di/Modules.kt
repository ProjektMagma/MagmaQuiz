package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.auth.data.AuthService
import com.github.projektmagma.magmaquiz.app.auth.data.UserRepository
import com.github.projektmagma.magmaquiz.app.auth.presentation.AuthViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.ServerConfigViewModel
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import com.github.projektmagma.magmaquiz.app.home.data.QuizRepository
import com.github.projektmagma.magmaquiz.app.home.data.QuizService
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizDetailsViewModel
import com.github.projektmagma.magmaquiz.app.home.presentation.QuizViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::AuthService)
    singleOf(::UserRepository)
    singleOf(::BaseUrlProvider)
    singleOf(::QuizService)
    singleOf(::QuizRepository)
    viewModelOf(::AuthViewModel)
    viewModelOf(::ServerConfigViewModel)
    viewModelOf(::RootViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::QuizDetailsViewModel)

    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json)
            }
            install(HttpCookies)
        }
    }
}

expect val platformModule: Module