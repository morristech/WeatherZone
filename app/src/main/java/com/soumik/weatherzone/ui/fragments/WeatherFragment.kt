package com.soumik.weatherzone.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.soumik.weatherzone.R
import com.soumik.weatherzone.data.models.CityUpdate
import com.soumik.weatherzone.data.models.ResponseWeather
import com.soumik.weatherzone.data.repository.local.CityRepository
import com.soumik.weatherzone.data.repository.local.LocationProvider
import com.soumik.weatherzone.data.repository.remote.WeatherRepository
import com.soumik.weatherzone.databinding.FragmentWeatherBinding
import com.soumik.weatherzone.databinding.LayoutAdditionalWeatherInfoBinding
import com.soumik.weatherzone.databinding.LayoutInfoBinding
import com.soumik.weatherzone.db.WeatherDatabase
import com.soumik.weatherzone.utils.*
import com.soumik.weatherzone.viewmodel.CityViewModel
import com.soumik.weatherzone.viewmodel.WeatherModel

class WeatherFragment : Fragment() {

    private var _weatherBinding: FragmentWeatherBinding? = null
    private val weatherBinding get() = _weatherBinding!!
    private lateinit var infoBinding: LayoutInfoBinding
    private lateinit var additionalInfoBinding: LayoutAdditionalWeatherInfoBinding

    private lateinit var weatherModel: WeatherModel
    private lateinit var cityModel: CityViewModel
    private lateinit var locationProvider: LocationProvider
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var cityRepo: CityRepository

    private var isGpsEnabled = false
    private var lat: String? = null
    private var lon: String? = null
    private var cityName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _weatherBinding = FragmentWeatherBinding.inflate(inflater, container, false)
        val view = weatherBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoBinding = weatherBinding.incInfoWeather
        additionalInfoBinding = infoBinding.incAdditionalWeatherInfo

        locationProvider = LocationProvider(requireActivity())
        weatherModel = ViewModelProvider(requireActivity()).get(WeatherModel::class.java)
        cityModel = ViewModelProvider(requireActivity()).get(CityViewModel::class.java)
        weatherRepo = WeatherRepository()
        cityRepo = CityRepository(WeatherDatabase.getDatabase(requireActivity())!!)

        GpsUtils(requireActivity()).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@WeatherFragment.isGpsEnabled = isGPSEnable
            }
        })
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _weatherBinding = null
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    private fun setUpObservers() {
        weatherModel.locationLiveData.observe(
            requireActivity(),
            {
                weatherModel.getWeatherByLocation(
                    weatherRepo,
                    it.latitude!!,
                    it.longitude!!
                )
            }
        )

        weatherModel.weatherByLocation.observe(
            requireActivity(),
            {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            infoBinding.root.visibility = View.VISIBLE
                            weatherBinding.progressBar.visibility = View.GONE
                            weatherBinding.animFailed.visibility = View.GONE
                            weatherBinding.animNetwork.visibility = View.GONE
                            setUpUI(it.data)
                            cityModel.updateSavedCities(cityRepo, CityUpdate(it.data?.id, 1))
                        }
                        Status.ERROR -> {
                            showFailedView(it.message)
                        }
                        Status.LOADING -> {
                            weatherBinding.progressBar.visibility = View.VISIBLE
                            weatherBinding.animFailed.visibility = View.GONE
                            weatherBinding.animNetwork.visibility = View.GONE
                        }
                    }
                }
            }
        )
    }

    private fun showFailedView(message: String?) {
        weatherBinding.progressBar.visibility = View.GONE
        infoBinding.root.visibility = View.GONE
        when (message) {
            "Network Failure" -> {
                weatherBinding.animFailed.visibility = View.GONE
                weatherBinding.animNetwork.visibility = View.VISIBLE
            }
            else -> {
                weatherBinding.animNetwork.visibility = View.GONE
                weatherBinding.animFailed.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(data: ResponseWeather?) {
        infoBinding.tvTemp.text = data?.main?.temp.toString()
        infoBinding.tvCityName.text = data?.name
        infoBinding.tvWeatherCondition.text = data?.weather!![0].main
        additionalInfoBinding.tvSunriseTime.text = data.sys.sunrise.unixTimestampToTimeString()
        additionalInfoBinding.tvSunsetTime.text = data.sys.sunset.unixTimestampToTimeString()
        additionalInfoBinding.tvRealFeelLabel.text =
            "${data.main.feelsLike}${getString(R.string.degree_celsius_symbol)}"
        additionalInfoBinding.tvCloudinessText.text = "${data.clouds.all}%"
        additionalInfoBinding.tvWindSpeedText.text = "${data.wind.speed}m/s"
        additionalInfoBinding.tvHumidityText.text = "${data.main.humidity}%"
        additionalInfoBinding.tvPressureText.text = "${data.main.pressure}hPa"
        additionalInfoBinding.tvVisibilityText.text = "${data.visibility}M"

        lat = data.coord.lat.toString()
        lon = data.coord.lon.toString()
        cityName = data.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGpsEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGpsEnabled -> showToast(requireActivity(), "Enable GPS", 1)
            isPermissionsGranted() -> startLocationUpdate()
            shouldShowRequestPermissionRationale() -> requestLocationPermission()
            else -> requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST
        )
    }

    private fun startLocationUpdate() {
        weatherModel.getCurrentLocation(locationProvider)
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    fun onAddButtonClicked(view: View) {
        // TODO: 1/1/21 Implement navigation action 
//        startActivity(Intent(this@MainActivity, SavedCityActivity::class.java))
    }

    fun onForecastButtonClicked(view: View) {
        // TODO: 1/1/21 Implement navigation action 
//        startActivity(
//            Intent(this@MainActivity, ForecastActivity::class.java)
//                .putExtra(ForecastActivity.LATITUDE, lat)
//                .putExtra(ForecastActivity.LONGITUDE, lon)
//                .putExtra(ForecastActivity.CITY_NAME, cityName)
//        )
    }

    fun onMoreOptionClicked(view: View) {
        // TODO: 1/1/21 Implement navigation action 
//        showMoreOptions(this@MainActivity)
    }
}
