package com.github.projektmagma.magmaquiz.di

import com.github.projektmagma.magmaquiz.data.UserRepository
import com.github.projektmagma.magmaquiz.presentation.AuthViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module { 
    singleOf(::UserRepository)
    viewModelOf(::AuthViewModel)
    
    single {
        HttpClient(OkHttp){
            install(ContentNegotiation){
                json(Json)
            }
            install(HttpCookies)
        }
    }
}