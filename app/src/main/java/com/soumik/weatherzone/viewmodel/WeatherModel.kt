package com.soumik.weatherzone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soumik.weatherzone.data.models.Coordinates
import com.soumik.weatherzone.data.models.ResponseWeather
import com.soumik.weatherzone.data.models.ResponseWeatherForecast
import com.soumik.weatherzone.data.repository.local.LocationProvider
import com.soumik.weatherzone.data.repository.remote.WeatherRepository
import com.soumik.weatherzone.utils.RequestCompleteListener
import com.soumik.weatherzone.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class WeatherModel : ViewModel() {

    private val tag = "WeatherModel"

    // location live data
    val locationLiveData = MutableLiveData<Coordinates>()
    val locationLiveDataFailure = MutableLiveData<String>()

    // weatherByLocation live data
    val weatherByLocation = MutableLiveData<Resource<ResponseWeather>>()

    // weatherByCityID live data
    val weatherByCityID = MutableLiveData<Resource<ResponseWeather>>()

    // weatherForecast live data
    val weatherForecast = MutableLiveData<Resource<ResponseWeatherForecast>>()

    fun getCurrentLocation(model: LocationProvider) {
        model.getUserCurrentLocation(object : RequestCompleteListener<Coordinates> {
            override fun onRequestCompleted(data: Coordinates) {
                locationLiveData.postValue(data)
            }

            override fun onRequestFailed(errorMessage: String?) {
                locationLiveDataFailure.postValue(errorMessage)
            }
        })
    }

    /**
     * Weather by Location call
     */
    fun getWeatherByLocation(model: WeatherRepository, lat: Double, lon: Double) {
        viewModelScope.launch { safeWeatherByLocationFetch(model, lat, lon) }
    }

    private suspend fun safeWeatherByLocationFetch(
        model: WeatherRepository,
        lat: Double,
        lon: Double
    ) {
        weatherByLocation.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherByLocation(lat, lon)
            weatherByLocation.postValue(handleWeatherResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherByLocation.postValue(
                    Resource.error(
                        null,
                        "Network Failure"
                    )
                )
                else -> weatherByLocation.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    /**
     * Weather by CityID call
     */
    fun getWeatherByCityID(model: WeatherRepository, id: String) {
        viewModelScope.launch { safeWeatherByCityIDFetch(model, id) }
    }

    private suspend fun safeWeatherByCityIDFetch(model: WeatherRepository, id: String) {
        weatherByCityID.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherByCityID(id)
            weatherByCityID.postValue(handleWeatherResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherByCityID.postValue(Resource.error(null, "Network Failure"))
                else -> weatherByCityID.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    private fun handleWeatherResponse(
        response: Response<ResponseWeather>
    ): Resource<ResponseWeather> {
        return if (response.isSuccessful) Resource.success(response.body()) else Resource.error(
            null,
            "Error: ${response.errorBody()}"
        )
    }

    /**
     * Weather Forecast call
     */
    fun getWeatherForecast(model: WeatherRepository, lat: Double, lon: Double, exclude: String) {
        viewModelScope.launch { safeWeatherForecastFetch(model, lat, lon, exclude) }
    }

    private suspend fun safeWeatherForecastFetch(
        model: WeatherRepository,
        lat: Double,
        lon: Double,
        exclude: String
    ) {
        weatherForecast.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherForecast(lat, lon, exclude)
            weatherForecast.postValue(handleWeatherForecast(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherForecast.postValue(Resource.error(null, "Network Failure"))
                else -> weatherForecast.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    private fun handleWeatherForecast(
        response: Response<ResponseWeatherForecast>
    ): Resource<ResponseWeatherForecast> {
        return if (response.isSuccessful) Resource.success(response.body()) else Resource.error(
            null,
            "Error: ${response.errorBody()}"
        )
    }
}
