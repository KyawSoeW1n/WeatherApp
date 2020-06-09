package com.kyawsoewin.weatherapp.ui

import android.app.Application
import com.kyawsoewin.weatherapp.network.RetrofitInstanceFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitInstanceFactory.newRetrofitInstance()
    }
}