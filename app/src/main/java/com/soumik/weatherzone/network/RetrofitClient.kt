package com.soumik.weatherzone.network

import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.soumik.weatherzone.BuildConfig
import com.soumik.weatherzone.utils.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

class RetrofitClient {

    companion object {

        private val retrofit by lazy {
            val httpClient =
                OkHttpClient.Builder().addInterceptor(QueryParameterAddInterceptor()).apply {
                    addInterceptor(
                        LoggingInterceptor.Builder()
                            .loggable(BuildConfig.DEBUG)
                            .setLevel(Level.BASIC)
                            .log(Platform.INFO)
                            .request("LOG")
                            .response("LOG")
                            .executor(Executors.newSingleThreadExecutor())
                            .build()
                    )
                }.build()

            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()
        }

        val weatherApiService: WeatherApiService by lazy {
            retrofit.create(WeatherApiService::class.java)
        }
    }
}
