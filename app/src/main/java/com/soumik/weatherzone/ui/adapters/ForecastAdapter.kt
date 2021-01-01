package com.soumik.weatherzone.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soumik.weatherzone.R
import com.soumik.weatherzone.data.models.Daily
import com.soumik.weatherzone.databinding.ItemForecastBinding
import com.soumik.weatherzone.utils.DiffUtilCallbackForecast
import com.soumik.weatherzone.utils.unixTimestampToDateTimeString
import com.soumik.weatherzone.utils.unixTimestampToTimeString

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.Holder>() {
    val differ = AsyncListDiffer(this, DiffUtilCallbackForecast())

    inner class Holder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val tvForecastTime = binding.tvTimeForecast
        val tvWeatherCondition = binding.tvWeatherCondition
        val ivWeatherIcon = binding.ivWeatherIcon
        val tvDayTemp = binding.incDayForecastInfo.tvDayTemp
        val tvEveTemp = binding.incDayForecastInfo.tvEveTemp
        val tvNightTemp = binding.incDayForecastInfo.tvNightTemp
        val tvMaxTemp = binding.incDayForecastInfo.tvMaxTemp
        val tvMinTemp = binding.incDayForecastInfo.tvMinTemp
        val tvMornFeel = binding.incDayForecastInfo.tvMornFeel
        val tvDayFeel = binding.incDayForecastInfo.tvDayFeel
        val tvEveFeel = binding.incDayForecastInfo.tvEveFeel
        val tvNightFeel = binding.incDayForecastInfo.tvNightFeel
        val tvSunriseTime = binding.incDayForecastInfo.tvSunriseTime
        val tvSunsetTime = binding.incDayForecastInfo.tvSunsetTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = differ.currentList[position]
        bindData(holder, data)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(holder: Holder, data: Daily?) {
        val weatherConditionIconUrl =
            "http://openweathermap.org/img/w/${data!!.weather[0].icon}.png"
        holder.apply {
            tvForecastTime.text = data.dt.unixTimestampToDateTimeString()
            val context = itemView.context
            if (!(context as Activity).isFinishing) Glide.with(context)
                .load(weatherConditionIconUrl)
                .into(ivWeatherIcon)
            tvWeatherCondition.text = data.weather[0].main
            tvDayTemp.text =
                "Day\n${data.temp.day}${context.getString(R.string.degree_celsius_symbol)}"
            tvEveTemp.text =
                "Evening\n${data.temp.eve}${context.getString(R.string.degree_celsius_symbol)}"
            tvNightTemp.text =
                "Night\n${data.temp.night}${context.getString(R.string.degree_celsius_symbol)}"
            tvMaxTemp.text =
                "Max\n${data.temp.max}${context.getString(R.string.degree_celsius_symbol)}"
            tvMinTemp.text =
                "Min\n${data.temp.min}${context.getString(R.string.degree_celsius_symbol)}"

            tvMornFeel.text =
                "Morning\n${data.feelsLike.morn}${context.getString(R.string.degree_celsius_symbol)}"
            tvDayFeel.text =
                "Day\n${data.feelsLike.day}${context.getString(R.string.degree_celsius_symbol)}"
            tvEveFeel.text =
                "Evening\n${data.feelsLike.eve}${context.getString(R.string.degree_celsius_symbol)}"
            tvNightFeel.text =
                "Night\n${data.feelsLike.night}${context.getString(R.string.degree_celsius_symbol)}"

            tvSunriseTime.text = data.sunrise.unixTimestampToTimeString()
            tvSunsetTime.text = data.sunset.unixTimestampToTimeString()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
