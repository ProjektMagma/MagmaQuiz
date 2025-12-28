package com.github.projektmagma.magmaquiz.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ApiDataStore(private val dataStore: DataStore<Preferences>) {


    suspend fun setSessionHeader(userSessionString: String): Boolean {
        try {
            dataStore.edit {
                val userSession = stringPreferencesKey("user_session")
                it[userSession] = userSessionString
            }
        } catch (_: IOException) {
            return false
        }
        return true
    }

    suspend fun getSessionHeader(): String {
        return dataStore.data.map {
            val userSession = stringPreferencesKey("user_session")
            it[userSession] ?: ""
        }.first()
    }
}