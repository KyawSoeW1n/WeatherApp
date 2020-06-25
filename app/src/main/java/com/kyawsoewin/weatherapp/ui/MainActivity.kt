package com.kyawsoewin.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kyawsoewin.weatherapp.Constants
import com.kyawsoewin.weatherapp.R
import com.kyawsoewin.weatherapp.network.Resource
import com.kyawsoewin.weatherapp.network.Status
import com.kyawsoewin.weatherapp.network.response.WeatherResponse
import com.kyawsoewin.weatherapp.utils.CommonUtils
import com.kyawsoewin.weatherapp.utils.PreferenceManager
import com.kyawsoewin.weatherapp.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    CompoundButton.OnCheckedChangeListener {

    private val mainViewModel: MainViewModel by viewModel()
    private val preferenceManager: PreferenceManager by inject()

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

    private val cityObserver = Observer<Resource<WeatherResponse>> {
        when (it.status) {
            Status.SUCCESS -> showData(
                it.data!!.name,
                it.data.main.temp,
                it.data.weatherList.getOrNull(0)?.weatherMain ?: "",
                it.data.weatherList.getOrNull(0)?.icon ?: "",
                it.data.wind.speed,
                it.data.main.humidity
            )
            Status.ERROR -> showError(it.message!!)
            Status.LOADING -> showLoading()
        }
    }

    private val locationObserver = Observer<Resource<WeatherResponse>> {
        when (it.status) {

            Status.SUCCESS -> showData(
                it.data!!.name,
                it.data.main.temp,
                it.data.weatherList.getOrNull(0)?.weatherMain ?: "",
                it.data.weatherList.getOrNull(0)?.icon ?: "",
                it.data.wind.speed,
                it.data.main.humidity
            )
            Status.ERROR -> showError(it.message!!)
            Status.LOADING -> showLoading()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        setUpListener()
        setUpObserver()
        checkUnit()
    }

    private fun checkUnit() {
        swTemperature.isChecked = preferenceManager.getPreferenceString(Constants.PREF_TYPE).equals(Constants.METRIC)
    }

    private fun showLoading() {
        swpRefresh.isRefreshing = true
    }

    private fun dismissLoading() {
        swpRefresh.isRefreshing = false
    }

    private fun showData(
        name: String,
        temp: String,
        weatherMain: String,
        icon: String,
        windSpeed: String,
        humidity: String
    ) {
        dismissLoading()
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

    private fun setUpObserver() {
        mainViewModel.weatherByLocation.observe(this, locationObserver)
        mainViewModel.weatherByCity.observe(this, cityObserver)
    }

    private fun setUpListener() {
        swpRefresh.setOnRefreshListener(this)
        swTemperature.setOnCheckedChangeListener(this)
    }

    private fun showError(message: String) {
        dismissLoading()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

        mainViewModel.getByLocation(location.latitude.toString(), location.longitude.toString())
    }

    fun searchByCity(v: View) {
        if (etCityName.text.toString().isNotEmpty()) {
            etCityName.clearFocus()
            mainViewModel.getByCity(etCityName.text.toString())
            CommonUtils.newInstance(this)
        } else
            Toast.makeText(this, " Enter City Name", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        if (etCityName.text.toString().isEmpty()) {
            getLocation()
        } else {
            mainViewModel.getByCity(etCityName.text.toString())
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            preferenceManager.putPreferenceString(Constants.PREF_TYPE, Constants.METRIC)
        } else {
            preferenceManager.putPreferenceString(Constants.PREF_TYPE, Constants.IMPERIAL)
        }
        onRefresh()
    }
}
