package com.kyawsoewin.weatherapp.di

import com.kyawsoewin.weatherapp.repo.WeatherRepository
import org.koin.dsl.module

val repoModule = module {
    factory { WeatherRepository(get(), get(), get()) }
}