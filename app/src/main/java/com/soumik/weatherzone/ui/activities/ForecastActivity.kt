package com.soumik.weatherzone.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.soumik.weatherzone.data.repository.remote.WeatherRepository
import com.soumik.weatherzone.databinding.ActivityForecastBinding
import com.soumik.weatherzone.databinding.LayoutToolbarBinding
import com.soumik.weatherzone.ui.adapters.ForecastAdapter
import com.soumik.weatherzone.utils.Status
import com.soumik.weatherzone.utils.lightStatusBar
import com.soumik.weatherzone.viewmodel.WeatherModel

class ForecastActivity : AppCompatActivity() {

    private lateinit var forecastBinding: ActivityForecastBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding

    private lateinit var viewModel: WeatherModel
    private lateinit var repository: WeatherRepository
    private lateinit var forecastAdapter: ForecastAdapter

    private lateinit var rvForecast: RecyclerView
    private lateinit var tvErrorMessage: TextView
    private lateinit var progressBar: LottieAnimationView
    private lateinit var animationNetworkView: LottieAnimationView
    private lateinit var animationFailedView: LottieAnimationView

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this, true)

        forecastBinding = ActivityForecastBinding.inflate(layoutInflater)
        val view = forecastBinding.root
        setContentView(view)

        toolbarBinding = forecastBinding.toolbarLayout

        viewModel = ViewModelProvider(this).get(WeatherModel::class.java)
        repository = WeatherRepository()
        forecastAdapter = ForecastAdapter()

        latitude = intent.getDoubleExtra(LATITUDE, Double.NEGATIVE_INFINITY)
        longitude = intent.getDoubleExtra(LONGITUDE, Double.NEGATIVE_INFINITY)
        cityName = intent.getStringExtra(CITY_NAME)

        rvForecast = forecastBinding.rvForecast
        tvErrorMessage = forecastBinding.tvErrorMsg
        progressBar = forecastBinding.progressBar
        animationNetworkView = forecastBinding.animNetwork
        animationFailedView = forecastBinding.animFailed

        toolbarBinding.tvToolTitle.text = cityName

        if (latitude != null && longitude != null) viewModel.getWeatherForecast(
            repository,
            latitude!!,
            longitude!!,
            EXCLUDE
        )

        setUpRecyclerView()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.weatherForecast.observe(
            this,
            {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            progressBar.visibility = View.GONE
                            tvErrorMessage.visibility = View.GONE
                            animationFailedView.visibility = View.GONE
                            animationNetworkView.visibility = View.GONE
                            rvForecast.visibility = View.VISIBLE
                            forecastAdapter.differ.submitList(it.data?.daily)
                        }
                        Status.ERROR -> {
                            showFailedView(it.message)
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
                            tvErrorMessage.visibility = View.GONE
                            rvForecast.visibility = View.GONE
                            animationFailedView.visibility = View.GONE
                            animationNetworkView.visibility = View.GONE
                        }
                    }
                }
            }
        )
    }

    private fun showFailedView(message: String?) {
        progressBar.visibility = View.GONE
        tvErrorMessage.visibility = View.GONE
        rvForecast.visibility = View.GONE
        when (message) {
            "Network Failure" -> {
                animationFailedView.visibility = View.GONE
                animationNetworkView.visibility = View.VISIBLE
            }
            else -> {
                animationNetworkView.visibility = View.GONE
                animationFailedView.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpRecyclerView() {
        rvForecast.apply {
            layoutManager = LinearLayoutManager(this@ForecastActivity)
            setHasFixedSize(true)
            adapter = forecastAdapter
        }
    }

    fun onBackButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    companion object {
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
        const val CITY_NAME = "city"
        const val EXCLUDE = "current,minutely,hourly"
    }
}
