package com.soumik.weatherzone.data.repository.local

import com.soumik.weatherzone.data.models.City
import com.soumik.weatherzone.data.models.CityUpdate
import com.soumik.weatherzone.db.WeatherDatabase

class CityRepository(private val database: WeatherDatabase) {

    suspend fun searchCities(key: String) = database.getCityDao().searchCity(key)
    suspend fun updateSavedCities(obj: CityUpdate) = database.getCityDao().updateSavedCity(obj)
    suspend fun getSavedCities(key: Int) = database.getCityDao().getSavedCity(key)
    suspend fun deleteSavedCities(city: City) = database.getCityDao().deleteSavedCity(city)
}
