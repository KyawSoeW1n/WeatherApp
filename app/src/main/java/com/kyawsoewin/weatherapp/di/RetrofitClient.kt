package com.kyawsoewin.weatherapp.di

import com.kyawsoewin.weatherapp.Constants
import com.kyawsoewin.weatherapp.network.AppInterceptor
import com.kyawsoewin.weatherapp.network.ResponseHandler
import com.kyawsoewin.weatherapp.network.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    factory { AppInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    factory { provideWeatherApi(get()) }
    factory { provideLoggingInterceptor() }
    factory { ResponseHandler()  }
    single { provideRetrofit(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(Constants.BASE_URL).client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
}

fun provideOkHttpClient(
    authInterceptor: AppInterceptor,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient().newBuilder().addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor).build()
}

fun provideWeatherApi(retrofit: Retrofit): WeatherApiService =
    retrofit.create(WeatherApiService::class.java)

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BASIC
    return logger
}