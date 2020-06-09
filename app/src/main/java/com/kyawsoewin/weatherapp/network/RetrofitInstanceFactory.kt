package com.kyawsoewin.weatherapp.network

import com.kyawsoewin.weatherapp.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstanceFactory {
    private var retrofit: Retrofit? = null
    private var apiService: WeatherApiService? = null
    fun newRetrofitInstance(): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder();

        val appInterceptor = AppInterceptor()
        okHttpClientBuilder.addInterceptor(appInterceptor)

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .client(okHttpClientBuilder.build())
                .build()
            apiService = retrofit!!.create(WeatherApiService::class.java)
        }

        return retrofit!!
    }

    fun newApiServiceInstance(): WeatherApiService? {
        return apiService
    }
}