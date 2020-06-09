package com.kyawsoewin.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kyawsoewin.weatherapp.network.response.WeatherResponse
import com.kyawsoewin.weatherapp.R
import com.kyawsoewin.weatherapp.network.RetrofitInstanceFactory
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val tvTemp by lazy {
        findViewById<TextView>(R.id.tvTemperature)
    }
    private val etCityName by lazy {
        findViewById<TextView>(R.id.etCityName)
    }

    private val tvCity by lazy {
        findViewById<TextView>(R.id.tvCityName)
    }

    private val imgImage by lazy {
        findViewById<ImageView>(R.id.ivImage)
    }

    private val apiService by lazy {
        RetrofitInstanceFactory.newApiServiceInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitInstanceFactory.newRetrofitInstance()
        requestPermission()
    }

    private fun executeNetworkCall(latitude: String, longitude: String) {
        apiService!!.getByCoordinate(latitude, longitude, "metric")
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { weatherResponse ->
                            val icon = weatherResponse.weatherList.getOrNull(0)?.icon ?: ""
                            showData(
                                weatherResponse.main.temp,
                                weatherResponse.name,
                                "https://openweathermap.org/img/wn/$icon@2x.png"
                            )
                        }
                    }
                }

            })

    }

    private fun executeNetworkCall(cityName: String) {

//        val apiService = retrofit.create(WeatherApiService::class.java)

        apiService!!.getByCity(cityName, "metric")
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { weatherResponse ->
                            val icon = weatherResponse.weatherList.getOrNull(0)?.icon ?: ""
                            showData(
                                weatherResponse.main.temp,
                                weatherResponse.name,
                                "https://openweathermap.org/img/wn/$icon@2x.png"
                            )
                        }
                    }
                }

            })

    }

    private fun showData(temp: String, name: String, icon: String) {
        tvCity.text = name
        tvTemp.text = temp
        Glide.with(this).load(icon).into(imgImage)
    }

    private fun requestPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    if (report.areAllPermissionsGranted()) {

                        val locationManager =
                            this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                        val location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            executeNetworkCall(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        } else {
                            Log.e("Location", "Null")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) { /* ... */
                }
            }).check()


    }

    fun searchByCity() {
        executeNetworkCall(etCityName.text.toString())
    }
}
