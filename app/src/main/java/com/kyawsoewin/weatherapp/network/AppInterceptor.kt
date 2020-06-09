package com.kyawsoewin.weatherapp.network

import com.kyawsoewin.weatherapp.Constants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AppInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val url = chain.request().url

        val newUrl = url.newBuilder()
            .addQueryParameter("appid", Constants.API_KEY)
            .build()

        return chain.proceed(Request.Builder().url(newUrl).build())
    }

}