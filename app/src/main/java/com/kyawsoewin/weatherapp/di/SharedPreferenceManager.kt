package com.kyawsoewin.weatherapp.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferenceModule = module {
    single<SharedPreferences> { androidContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE) }
}