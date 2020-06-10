package com.kyawsoewin.weatherapp.di

import com.kyawsoewin.weatherapp.ui.MainActivity
import org.koin.dsl.module

val uiModule = module {
    factory { MainActivity() }
}