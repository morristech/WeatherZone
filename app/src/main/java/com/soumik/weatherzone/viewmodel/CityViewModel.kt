package com.soumik.weatherzone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soumik.weatherzone.data.models.City
import com.soumik.weatherzone.data.models.CityUpdate
import com.soumik.weatherzone.data.repository.local.CityRepository
import com.soumik.weatherzone.utils.Resource
import com.soumik.weatherzone.utils.info
import kotlinx.coroutines.launch
import java.io.IOException

class CityViewModel : ViewModel() {
    private val tag = "CityViewModel"

    // cityBySearch live data
    val cityByQuery = MutableLiveData<Resource<List<City>>>()

    // savedCities live data
    val savedCities = MutableLiveData<List<City>>()

    /**
     * City by query call
     */
    fun getCityByQuery(model: CityRepository, query: String) =
        viewModelScope.launch { safeCityByQueryFetch(model, query) }

    private suspend fun safeCityByQueryFetch(model: CityRepository, query: String) {
        cityByQuery.postValue(Resource.loading(null))
        try {
            val response = model.searchCities(key = query)
            cityByQuery.postValue(handleCitySearch(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> cityByQuery.postValue(Resource.error(null, "Network Failure"))
                else -> {
                    cityByQuery.postValue(Resource.error(null, t.localizedMessage))
                    com.soumik.weatherzone.utils.error(tag, t.localizedMessage!!)
                }
            }
        }
    }

    private fun handleCitySearch(response: List<City>): Resource<List<City>> =
        Resource.success(response)

    /**
     * Update City call
     */
    fun updateSavedCities(model: CityRepository, obj: CityUpdate) = viewModelScope.launch {
        try {
            val info = model.updateSavedCities(obj)
            info(tag, "Success: Updating City DB: $info")
        } catch (e: Exception) {
            e.stackTrace
            com.soumik.weatherzone.utils.error(
                tag,
                "Error: Updating City DB: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Saved City call
     */
    fun getSavedCities(model: CityRepository, key: Int) = viewModelScope.launch {
        try {
            val cities = model.getSavedCities(key)
            info(tag, "Success: Getting Saves City DB: $cities")
            savedCities.postValue(cities)
        } catch (e: Exception) {
            e.stackTrace
            com.soumik.weatherzone.utils.error(
                tag,
                "Error: Updating City DB: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Delete City call
     */
    fun deleteSavedCities(model: CityRepository, city: City) = viewModelScope.launch {
        try {
            model.deleteSavedCities(city)
        } catch (e: Exception) {
            e.stackTrace
            com.soumik.weatherzone.utils.error(
                tag,
                "Error: Deleting City DB: ${e.localizedMessage}"
            )
        }
    }
}
