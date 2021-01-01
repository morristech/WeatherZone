package com.soumik.weatherzone.network

import com.soumik.weatherzone.data.models.ResponseWeather
import com.soumik.weatherzone.data.models.ResponseWeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<ResponseWeather>

    @GET("weather")
    suspend fun getWeatherByCityID(
        @Query("id") query: String
    ): Response<ResponseWeather>

    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") query: String
    ): Response<ResponseWeather>

    @GET("onecall")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String
    ): Response<ResponseWeatherForecast>
}
