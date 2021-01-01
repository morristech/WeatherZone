package com.soumik.weatherzone.network

import com.soumik.weatherzone.BuildConfig
import com.soumik.weatherzone.WeatherZone
import com.soumik.weatherzone.utils.PrefManager
import okhttp3.Interceptor
import okhttp3.Response

class QueryParameterAddInterceptor : Interceptor {

    val context = WeatherZone.context
    private val prefManager = PrefManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url().newBuilder()
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            .addQueryParameter("units", prefManager.tempUnit)
            .build()

        val request = chain.request().newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}
