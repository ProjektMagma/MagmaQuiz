package com.github.projektmagma.magmaquiz.app.core.util

import com.github.projektmagma.magmaquiz.app.core.data.ServerConfigDataStore

class BaseUrlProvider(
    private val serverConfigDataStore: ServerConfigDataStore
) {
    suspend fun getBaseUrl(): String {
        val config = serverConfigDataStore.getServerConfig()
        val port = if (!config.port.isEmpty()) ":${config.port}" else ""
        return "${config.protocol.name}://${config.ip}$port"
    }
}