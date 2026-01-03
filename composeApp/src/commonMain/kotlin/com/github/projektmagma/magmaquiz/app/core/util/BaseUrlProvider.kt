package com.github.projektmagma.magmaquiz.app.core.util

import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigDataStore

class BaseUrlProvider(
    private val serverConfigDataStore: ServerConfigDataStore
) {
    suspend fun getBaseUrl(): String {
        val config = serverConfigDataStore.getServerConfig()
        return "${config.protocol.name}://${config.ip}:${config.port}"
    }
}