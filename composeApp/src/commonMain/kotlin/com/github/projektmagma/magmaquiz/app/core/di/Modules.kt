package com.github.projektmagma.magmaquiz.app.core.di

import com.github.projektmagma.magmaquiz.app.core.data.ApiDataStore
import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigRepository
import com.github.projektmagma.magmaquiz.app.core.data.database.ServerConfigDao
import com.github.projektmagma.magmaquiz.app.core.data.database.getRoomDatabase
import com.github.projektmagma.magmaquiz.app.core.data.database.getServerConfigDao
import com.github.projektmagma.magmaquiz.app.core.presentation.RootViewModel
import com.github.projektmagma.magmaquiz.app.core.presentation.ServerConfigViewModel
import com.github.projektmagma.magmaquiz.shared.data.domain.CustomHeaders
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::ServerConfigViewModel)
    viewModelOf(::RootViewModel)
    
    singleOf(::ServerConfigRepository)
    single { getRoomDatabase(get()) }
    single { getServerConfigDao(get()) }

    single {
        HttpClient(CIO) {
            val defaultCredentialsPlugin = createClientPlugin("DefaultCredentialsPlugin"){
                onRequest { request, _ ->
                    val config = get<ServerConfigDao>().getSelectedConfig()
                    val sessionHeader = get<ApiDataStore>().getSessionHeader()
                    
                    val isWsRequest = request.url.protocol == URLProtocol.WSS
                    
                    request.url {
                        protocol = when {
                            isWsRequest -> URLProtocol.WS
                            else -> URLProtocol.createOrDefault(config.protocol.toString())
                        }
                        host = config.ip
                        port = config.port.toIntOrNull() ?: 0
                        path(request.url.encodedPath)
                    }
                    
                    if (sessionHeader.isNotBlank()){
                        request.header(CustomHeaders.UserSession, sessionHeader)
                    }
                }
            }
            
            install(ContentNegotiation) {
                json(Json)
            }
            install(HttpCookies)
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = "KtorClient", message = message)
                    }
                }
                level = LogLevel.HEADERS
            }
            install(WebSockets)
            install(defaultCredentialsPlugin)
        }
    }
}


expect val platformModule: Module