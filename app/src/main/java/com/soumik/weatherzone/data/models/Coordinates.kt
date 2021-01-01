package com.soumik.weatherzone.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(
    @ColumnInfo(name = "coord_lon")
    val longitude: Double? = Double.NEGATIVE_INFINITY,
    @ColumnInfo(name = "coord_lat")
    val latitude: Double? = Double.NEGATIVE_INFINITY
): Parcelable
