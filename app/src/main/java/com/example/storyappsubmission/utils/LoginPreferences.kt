package com.example.storyappsubmission.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val LOGIN_TOKEN = stringPreferencesKey("token")
    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[LOGIN_TOKEN] ?: "null"
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[LOGIN_TOKEN] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it.clear()

        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}