package com.kyawsoewin.weatherapp.repo

import com.kyawsoewin.weatherapp.network.Resource
import com.kyawsoewin.weatherapp.network.ResponseHandler
import com.kyawsoewin.weatherapp.network.WeatherApiService
import com.kyawsoewin.weatherapp.network.response.WeatherResponse


open class WeatherRepository(
    private val weatherApi: WeatherApiService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getWeatherByCity(location: String): Resource<WeatherResponse> {
        return try {
            val response = weatherApi.getByCity(location, "metric")
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getWeatherByLocation(latitude: String,longitude : String): Resource<WeatherResponse> {
        return try {
            val response = weatherApi.getByLocation(latitude,longitude, "metric")
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}