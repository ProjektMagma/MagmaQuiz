package com.github.projektmagma.magmaquiz.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.projektmagma.magmaquiz.presentation.Protocols
import com.github.projektmagma.magmaquiz.presentation.model.server.ServerConfig
import kotlinx.coroutines.flow.first

class ServerConfigDataStore(private val dataStore: DataStore<Preferences>) {
    companion object {
        val SERVER_IP_KEY = stringPreferencesKey("ip")
        val SERVER_PORT_KEY = stringPreferencesKey("port")
        val SERVER_PROTOCOL_KEY = stringPreferencesKey("protocol")
    }

    suspend fun setServerConfiguration(serverConfig: ServerConfig): Boolean {
        try {
            dataStore.edit {
                it[SERVER_IP_KEY] = serverConfig.ip
                it[SERVER_PORT_KEY] = serverConfig.port
                it[SERVER_PROTOCOL_KEY] = serverConfig.protocol.name
            }
        } catch (_: IOException) {
            return false
        }
        return true
    }

    suspend fun getServerConfig(): ServerConfig {
        val preferences = dataStore.data.first()
        return ServerConfig(
            ip = preferences[SERVER_IP_KEY] ?: "",
            port = preferences[SERVER_PORT_KEY] ?: "",
            protocol = Protocols.valueOf(preferences[SERVER_PROTOCOL_KEY] ?: Protocols.HTTPS.name)
        )
    }
}