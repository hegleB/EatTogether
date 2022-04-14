package com.qure.eattogether2.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefrences")

class UserPrefrence(private val context: Context) {

    //1 : Define prefernce Key
    val IS_LOGIN_KEY = booleanPreferencesKey("is_login")

    //2 : Read value from datastore using Key
    val isLogin: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[IS_LOGIN_KEY] ?: false
        } as Flow<Boolean>

    //4 : Write value to datastore using key
    suspend fun setLoginStatus(isLogin:Boolean) {
        context.dataStore.edit { prefrences ->
            prefrences[IS_LOGIN_KEY] = isLogin
        }
    }

    //5. If you want to clear data store
    @Suppress("unused")
    suspend fun clearDataStore() {
        context.dataStore.edit {
            it.clear()
        }
    }

}