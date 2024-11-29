package com.example.main_project

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension property for DataStore initialization
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    // Keys for storing token and role
    private val TOKEN_KEY = stringPreferencesKey("user_token")
    private val ROLE_KEY = stringPreferencesKey("user_role")

    // Save token to DataStore
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Save role to DataStore
    suspend fun saveRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role
        }
    }

    // Retrieve the token from DataStore as a Flow
    fun getToken(): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[TOKEN_KEY] }
    }

    // Retrieve the role from DataStore as a Flow
    fun getRole(): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[ROLE_KEY] }
    }

    // Delete both token and role from DataStore
    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(ROLE_KEY)
        }
    }

    // Check if token exists (used for validating token presence)
    suspend fun checkToken(): Boolean {
        val storedToken = getToken().first()  // Collects the flow synchronously
        return storedToken != null
    }

    // Utility function to log the stored token for debugging
    suspend fun logStoredToken() {
        val storedToken = getToken().first()
        Log.d("DataStoreManager", "Stored Token: $storedToken")
    }
}
