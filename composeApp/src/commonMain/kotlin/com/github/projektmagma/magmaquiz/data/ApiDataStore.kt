package com.github.projektmagma.magmaquiz.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ApiDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val USER_SESSION_KEY = stringPreferencesKey("user_session")
    }

    suspend fun setSessionHeader(userSessionString: String): Boolean {
        try {
            dataStore.edit {
                it[USER_SESSION_KEY] = userSessionString
            }
        } catch (_: IOException) {
            return false
        }
        return true
    }

    suspend fun getSessionHeader(): String {
        return dataStore.data.map {
            it[USER_SESSION_KEY] ?: ""
        }.first()
    }
}