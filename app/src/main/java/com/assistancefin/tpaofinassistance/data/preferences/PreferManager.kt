package com.assistancefin.tpaofinassistance.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_LINK = "key_link"

        private const val KEY_WATCHED = "key_link_tpao"
    }

    fun saveLink(link: String) {
        preferences.edit().putString(KEY_LINK, link).apply()
    }

    fun getLink(): String {
        return preferences.getString(KEY_LINK, "") ?: ""
    }

//    fun saveIsWatched(isWatched: Boolean) {
//        preferences.edit().putBoolean(KEY_WATCHED, isWatched).apply()
//    }
//
//    fun getIsWatched(): Boolean {
//        return preferences.getBoolean(KEY_WATCHED, false)
//    }

}