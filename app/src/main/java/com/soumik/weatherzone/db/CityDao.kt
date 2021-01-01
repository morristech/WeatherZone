package com.soumik.weatherzone.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.soumik.weatherzone.data.models.City
import com.soumik.weatherzone.data.models.CityUpdate

@Dao
interface CityDao {

    @Query("SELECT * FROM saved_city WHERE name LIKE :key || '%'")
    suspend fun searchCity(key: String): List<City>

    @Update(entity = City::class)
    suspend fun updateSavedCity(vararg obj: CityUpdate): Int

    @Query("SELECT * FROM saved_city WHERE isSaved= :key")
    suspend fun getSavedCity(key: Int): List<City>

    @Delete
    suspend fun deleteSavedCity(city: City)
}
