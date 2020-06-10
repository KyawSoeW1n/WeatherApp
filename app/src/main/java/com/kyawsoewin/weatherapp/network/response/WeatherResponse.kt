package com.kyawsoewin.weatherapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "weather") var weatherList: List<WeatherResponseWeather>,
    @Json(name = "main") var main: WeatherResponseMain,
    @Json(name = "wind") var wind: WeatherResponseWind,
    @Json(name = "name") var name: String
) {

}

@JsonClass(generateAdapter = true)
data class WeatherResponseWeather(
    @Json(name = "icon") val icon: String,
    @Json(name = "main") val weatherMain: String
) {
}

@JsonClass(generateAdapter = true)
data class WeatherResponseWind(@Json(name = "speed") val speed: String) {
}

@JsonClass(generateAdapter = true)
data class WeatherResponseMain(
    @Json(name = "temp") val temp: String,
    @Json(name = "humidity") val humidity: String
) {
}