package com.kyawsoewin.weatherapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kyawsoewin.weatherapp.Constants
import com.kyawsoewin.weatherapp.utils.PreferenceManager
import org.koin.dsl.module

val sharedPreferenceModule = module {
    single { provideSharedPreference(get()) }

    single { PreferenceManager(get()) }
}

fun provideSharedPreference(app: Application): SharedPreferences =
    app.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)