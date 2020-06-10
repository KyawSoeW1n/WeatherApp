package com.kyawsoewin.weatherapp.di

import com.kyawsoewin.weatherapp.viewmodel.MainViewModel
import org.koin.dsl.module


val modelModule = module {
    factory { MainViewModel(get()) }
}