package com.kyawsoewin.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kyawsoewin.weatherapp.Constants
import com.kyawsoewin.weatherapp.R
import com.kyawsoewin.weatherapp.network.RetrofitInstanceFactory
import com.kyawsoewin.weatherapp.network.response.WeatherResponse
import com.kyawsoewin.weatherapp.utils.CommonUtils
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    CompoundButton.OnCheckedChangeListener {

    private var temperature: String = "metric"
    private val tvTemp by lazy {
        findViewById<TextView>(R.id.tvTemperature)
    }
    private val tvHumidityValue by lazy {
        findViewById<TextView>(R.id.tvHumidityValue)
    }

    private val tvWindValue by lazy {
        findViewById<TextView>(R.id.tvWindValue)
    }
    private val swpRefresh by lazy {
        findViewById<SwipeRefreshLayout>(R.id.swpRefresh)
    }

    private val imvBackground by lazy {
        findViewById<ImageView>(R.id.imv_background)
    }

    private val etCityName by lazy {
        findViewById<TextView>(R.id.etCityName)
    }

    private val swTemperature by lazy {
        findViewById<Switch>(R.id.swTemperature)
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
        setUpListener()
    }

    private fun setUpListener() {
        swpRefresh.setOnRefreshListener(this)
        swTemperature.setOnCheckedChangeListener(this)
    }

    private fun executeNetworkCall(latitude: String, longitude: String) {
        swpRefresh.isRefreshing = true
        apiService!!.getByCoordinate(latitude, longitude, temperature)
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                    showError()
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { weatherResponse ->
                            val icon = weatherResponse.weatherList.getOrNull(0)?.icon ?: ""
                            val main = weatherResponse.weatherList.getOrNull(0)?.weatherMain ?: ""
                            showData(
                                weatherResponse.main.temp,
                                weatherResponse.name,
                                "https://openweathermap.org/img/wn/$icon@2x.png",
                                weatherResponse.main.humidity,
                                weatherResponse.wind.speed,
                                main
                            )
                        }
                    } else {
                        showError()
                    }
                }

            })

    }

    private fun executeNetworkCall(cityName: String) {
        swpRefresh.isRefreshing = true
        apiService!!.getByCity(cityName, "metric")
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                    showError()
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { weatherResponse ->
                            val icon = weatherResponse.weatherList.getOrNull(0)?.icon ?: ""
                            val main = weatherResponse.weatherList.getOrNull(0)?.weatherMain ?: ""
                            showData(
                                weatherResponse.main.temp,
                                weatherResponse.name,
                                "https://openweathermap.org/img/wn/$icon@2x.png",
                                weatherResponse.main.humidity,
                                weatherResponse.wind.speed,
                                main
                            )
                        }
                    } else {
                        showError()
                    }
                }

            })

    }

    private fun showError() {
        if (swpRefresh.isRefreshing)
            swpRefresh.isRefreshing = false
        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
    }

    private fun showData(
        temp: String,
        name: String,
        icon: String,
        humidity: String,
        windSpeed: String,
        weatherMain: String
    ) {
        if (swpRefresh.isRefreshing) {
            swpRefresh.isRefreshing = false
        }
        tvCity.text = name
        tvTemp.text = getString(R.string.celsius, temp)
        tvHumidityValue.text = humidity
        tvWindValue.text = getString(R.string.wind_speed, windSpeed)
        Glide.with(this).load(icon).into(imgImage)


        when (weatherMain) {
            "Thunderstorm" -> {
                Glide.with(this).load(Constants.THUNDER_STORM).into(imvBackground)
            }
            "Drizzle" -> {
                Glide.with(this).load(Constants.RAIN).into(imvBackground)
            }
            "Rain" -> {
                Glide.with(this).load(Constants.RAIN).into(imvBackground)
            }
            "Snow" -> {
                Glide.with(this).load(Constants.SNOW).into(imvBackground)
            }
            "Clouds" -> {
                Glide.with(this).load(Constants.CLOUD).into(imvBackground)
            }
            "Clear" -> {
                Glide.with(this).load(Constants.CLEAR_SKY).into(imvBackground)
            }
            else -> {
                Glide.with(this).load(Constants.DEFAULT_BACKGROUND).into(imvBackground)
            }
        }
        Glide.with(this).load(icon).into(imgImage)
    }

    private fun requestPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    if (report.areAllPermissionsGranted()) {
                        getLocation();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) { /* ... */
                }
            }).check()


    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
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

    fun searchByCity(v: View) {
        if (etCityName.text.toString().isNotEmpty()) {
            executeNetworkCall(etCityName.text.toString())
            etCityName.clearFocus()
            CommonUtils.newInstance(this)
        } else
            Toast.makeText(this, " Enter City Name", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            temperature = "metric"
            if (etCityName.text.toString().isEmpty()) {
                getLocation()
            } else {
                executeNetworkCall(etCityName.text.toString())
            }
        } else {
            temperature = "imperial"
            if (etCityName.text.toString().isEmpty()) {
                getLocation()
            } else {
                executeNetworkCall(etCityName.text.toString())
            }
        }
    }
}
