package com.example.main_project

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("user_token")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[TOKEN_KEY]
            }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun checkToken() {
        val storedToken = getToken().first()
        val expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJraWNhZ2lzNDkxQG1lcm90eC5jb20iLCJpYXQiOjE3MzI2MzMyMzMsImV4cCI6MTczMjcxOTYzM30.hC5hAWobf4Nzlz3eOyJ3I8-IjAn3FcIiywe0X-fTzsQ"

        if (storedToken == expectedToken) {
            Log.d("DataStoreManager", "Stored token matches the expected token")
        } else {
            Log.e("DataStoreManager", "Stored token does not match the expected token")
            Log.d("DataStoreManager", "Stored token: $storedToken")
            Log.d("DataStoreManager", "Expected token: $expectedToken")
        }
    }
}

