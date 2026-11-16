package com.cooperrivertrading.claudecontroller

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object Prefs {
    private const val FILE = "controller_prefs"
    private const val KEY_API_KEY = "api_key"
    private const val KEY_FORGE_URL = "forge_url"

    private fun prefs(ctx: Context) = EncryptedSharedPreferences.create(
        ctx,
        FILE,
        MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getApiKey(ctx: Context): String = prefs(ctx).getString(KEY_API_KEY, "") ?: ""
    fun getForgeUrl(ctx: Context): String = prefs(ctx).getString(KEY_FORGE_URL, "http://localhost:8780") ?: "http://localhost:8780"

    fun saveApiKey(ctx: Context, key: String) = prefs(ctx).edit().putString(KEY_API_KEY, key).apply()
    fun saveForgeUrl(ctx: Context, url: String) = prefs(ctx).edit().putString(KEY_FORGE_URL, url).apply()

    fun hasApiKey(ctx: Context) = getApiKey(ctx).startsWith("sk-ant-")
}
