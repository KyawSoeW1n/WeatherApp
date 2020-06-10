package com.kyawsoewin.weatherapp.viewmodel

import androidx.lifecycle.*
import com.kyawsoewin.weatherapp.network.Resource
import com.kyawsoewin.weatherapp.repo.WeatherRepository
import kotlinx.coroutines.Dispatchers


class MainViewModel(
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    private val cityLiveData = MutableLiveData<String>()
    private val locationLiveData = MutableLiveData<String>()


    fun getByCity(input: String) {
        cityLiveData.value = input
    }

    fun getByLocation(latitude: String, longitude: String) {
//        val location = Location(latitude, longitude)
//        locationLiveData.value = location
    }


    var weatherByCity = cityLiveData.switchMap { city ->
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            emit(weatherRepo.getWeatherByCity(city))
        }
    }
}
