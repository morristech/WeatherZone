package com.soumik.weatherzone.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class CityUpdate(
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "isSaved")
    var isSaved: Int? = null
)
