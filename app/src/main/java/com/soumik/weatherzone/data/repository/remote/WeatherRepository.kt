package com.soumik.weatherzone.data.repository.remote

import com.soumik.weatherzone.network.RetrofitClient

class WeatherRepository {

    suspend fun getWeatherByLocation(lat: Double, lon: Double) =
        RetrofitClient.weatherApiService.getWeatherByLocation(lat, lon)
    suspend fun getWeatherByCityID(id: String) =
        RetrofitClient.weatherApiService.getWeatherByCityID(id)
    suspend fun getWeatherByCityName(cityName: String) =
        RetrofitClient.weatherApiService.getWeatherByCityName(cityName)
    suspend fun getWeatherForecast(lat: Double, lon: Double, exclude: String) =
        RetrofitClient.weatherApiService.getWeatherForecast(lat, lon, exclude)
}
