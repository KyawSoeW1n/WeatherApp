package com.kyawsoewin.weatherapp.repo

import com.kyawsoewin.weatherapp.Constants
import com.kyawsoewin.weatherapp.network.Resource
import com.kyawsoewin.weatherapp.network.ResponseHandler
import com.kyawsoewin.weatherapp.network.WeatherApiService
import com.kyawsoewin.weatherapp.network.response.WeatherResponse
import com.kyawsoewin.weatherapp.utils.PreferenceManager


open class WeatherRepository(
    private val weatherApi: WeatherApiService,
    private val responseHandler: ResponseHandler,
    private val preferenceManager: PreferenceManager
) {

    suspend fun getWeatherByCity(location: String): Resource<WeatherResponse> {
        return try {
            val response = weatherApi.getByCity(
                location,
                preferenceManager.getPreferenceString(Constants.PREF_TYPE)!!
            )
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getWeatherByLocation(
        latitude: String,
        longitude: String
    ): Resource<WeatherResponse> {
        return try {
            val response = weatherApi.getByLocation(
                latitude,
                longitude,
                preferenceManager.getPreferenceString(Constants.PREF_TYPE)!!
            )
            return responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}