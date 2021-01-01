package com.soumik.weatherzone.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soumik.weatherzone.utils.TABLE_CITY
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = TABLE_CITY
)
@Parcelize
data class City(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String? = "",

    @ColumnInfo(name = "state")
    var state: String = "",

    @ColumnInfo(name = "country")
    var country: String? = "",

    @Embedded
    val coordinates: Coordinates,

    @ColumnInfo(name = "isSaved")
    var isSaved: Int? = null
): Parcelable
