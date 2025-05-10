package com.mobile.wanderwallet.data.repository

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface TokenProvider {
    fun getAccessToken(): String?
    fun setAccessToken(token: String)
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String)
    fun clearTokens()
}

class SharedPrefsTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
): TokenProvider {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    override fun setAccessToken(token: String) {
        prefs.edit { putString("access_token", token) }
    }

    override fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    override fun setRefreshToken(token: String) {
        prefs.edit { putString("refresh_token", token) }
    }

    override fun clearTokens() {
        prefs.edit { clear() }
    }
}