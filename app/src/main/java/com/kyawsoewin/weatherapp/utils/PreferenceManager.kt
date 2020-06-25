package com.kyawsoewin.weatherapp.utils

import android.content.SharedPreferences


class PreferenceManager(private val sharedPreferences: SharedPreferences) {
    fun getPreferenceString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getPreferenceInteger(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getPreferenceBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putPreferenceString(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putPreferenceInteger(key: String?, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun putPreferenceBoolean(key: String?, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun removePreferenceValue(key: String?) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun removeAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}
