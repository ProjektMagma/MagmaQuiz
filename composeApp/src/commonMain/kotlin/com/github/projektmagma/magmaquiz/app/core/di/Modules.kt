package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.ServerConfigViewModel
import com.github.projektmagma.magmaquiz.app.core.util.BaseUrlProvider
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::BaseUrlProvider)

    viewModelOf(::ServerConfigViewModel)
    viewModelOf(::RootViewModel)


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