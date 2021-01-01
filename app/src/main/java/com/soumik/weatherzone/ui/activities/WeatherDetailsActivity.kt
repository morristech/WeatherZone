package com.soumik.weatherzone.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.soumik.weatherzone.R
import com.soumik.weatherzone.data.models.ResponseWeather
import com.soumik.weatherzone.data.repository.remote.WeatherRepository
import com.soumik.weatherzone.databinding.ActivityWeatherDetailsBinding
import com.soumik.weatherzone.databinding.LayoutAdditionalWeatherInfoBinding
import com.soumik.weatherzone.databinding.LayoutInfoBinding
import com.soumik.weatherzone.utils.Status
import com.soumik.weatherzone.utils.unixTimestampToTimeString
import com.soumik.weatherzone.viewmodel.WeatherModel

class WeatherDetailsActivity : AppCompatActivity() {

    private lateinit var weatherDetailsBinding: ActivityWeatherDetailsBinding
    private lateinit var includedInfoBinding: LayoutInfoBinding
    private lateinit var additionalInfoBinding: LayoutAdditionalWeatherInfoBinding

    private lateinit var viewModel: WeatherModel
    private lateinit var weatherRepo: WeatherRepository

    private lateinit var progressBar: LottieAnimationView
    private lateinit var animationNetworkView: LottieAnimationView
    private lateinit var animationFailedView: LottieAnimationView

    private var cityID: String? = null
    private var latitudeString: String? = null
    private var longitudeString: String? = null
    private var cityName: String? = null

    companion object {
        const val CITY_ID = "city_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherDetailsBinding = ActivityWeatherDetailsBinding.inflate(layoutInflater)
        val view = weatherDetailsBinding.root
        setContentView(view)

        includedInfoBinding = weatherDetailsBinding.incInfoWeather
        additionalInfoBinding = includedInfoBinding.incAdditionalWeatherInfo

        viewModel = ViewModelProvider(this@WeatherDetailsActivity).get(WeatherModel::class.java)
        weatherRepo = WeatherRepository()

        cityID = intent.getStringExtra(CITY_ID)
        viewModel.getWeatherByCityID(weatherRepo, cityID!!)

        progressBar = weatherDetailsBinding.progressBar
        animationNetworkView = weatherDetailsBinding.animNetwork
        animationFailedView = weatherDetailsBinding.animFailed

        includedInfoBinding.ivAdd.setImageResource(R.drawable.ic_arrow_back_white)
        includedInfoBinding.ivMore.visibility = View.GONE

        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.weatherByCityID.observe(
            this@WeatherDetailsActivity,
            {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            includedInfoBinding.root.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            animationNetworkView.visibility = View.GONE
                            animationFailedView.visibility = View.GONE
                            setUpUI(it.data)
                        }
                        Status.ERROR -> {
                            showFailedView(it.message)
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
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
        includedInfoBinding.root.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private fun setUpUI(data: ResponseWeather?) {
        includedInfoBinding.tvTemp.text = data?.main?.temp.toString()
        includedInfoBinding.tvCityName.text = data?.name
        includedInfoBinding.tvWeatherCondition.text = data?.weather!![0].main
        additionalInfoBinding.tvSunriseTime.text = data.sys.sunrise.unixTimestampToTimeString()
        additionalInfoBinding.tvSunsetTime.text = data.sys.sunset.unixTimestampToTimeString()
        additionalInfoBinding.tvRealFeelText.text =
            "${data.main.feelsLike}${getString(R.string.degree_celsius_symbol)}"
        additionalInfoBinding.tvCloudinessText.text = "${data.clouds.all}%"
        additionalInfoBinding.tvWindSpeedText.text = "${data.wind.speed}m/s"
        additionalInfoBinding.tvHumidityText.text = "${data.main.humidity}%"
        additionalInfoBinding.tvPressureText.text = "${data.main.pressure}hPa"
        additionalInfoBinding.tvVisibilityText.text = "${data.visibility}M"

        latitudeString = data.coord.lat.toString()
        longitudeString = data.coord.lon.toString()
        cityName = data.name
    }

    fun onAddButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    fun onForecastButtonClicked(view: View) {
        startActivity(
            Intent(this@WeatherDetailsActivity, ForecastActivity::class.java)
                .putExtra(ForecastActivity.LATITUDE, latitudeString)
                .putExtra(ForecastActivity.LONGITUDE, longitudeString)
                .putExtra(ForecastActivity.CITY_NAME, cityName)
        )
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
