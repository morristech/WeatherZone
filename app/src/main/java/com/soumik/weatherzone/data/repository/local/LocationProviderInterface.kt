package com.soumik.weatherzone.data.repository.local

import com.soumik.weatherzone.data.models.Coordinates
import com.soumik.weatherzone.utils.RequestCompleteListener

interface LocationProviderInterface {
    fun getUserCurrentLocation(callback: RequestCompleteListener<Coordinates>)
}
