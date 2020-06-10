package com.kyawsoewin.weatherapp.network

import android.os.IInterface
import com.kyawsoewin.weatherapp.network.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService : IInterface {
    @GET("weather/")
    suspend fun getByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") unit: String
    ): WeatherResponse

    @GET("weather/")
    suspend fun getByCity(
        @Query("q") cityName: String,
        @Query("units") unit: String
    ): WeatherResponse
}