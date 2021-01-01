package com.soumik.weatherzone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.soumik.weatherzone.data.models.City
import com.soumik.weatherzone.databinding.ItemSavedCityBinding
import com.soumik.weatherzone.utils.DiffUtilCallback

class SavedCityAdapter : RecyclerView.Adapter<SavedCityAdapter.Holder>() {

    val differ = AsyncListDiffer(this, DiffUtilCallback())
    private var onItemClickListener: ((City) -> Unit)? = null

    fun setOnItemClickListener(listener: (City) -> Unit) {
        onItemClickListener = listener
    }

    class Holder(
        private val binding: ItemSavedCityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.tvCityNameSearch
        val countryName = binding.tvCountryNameSearch
        val temperature = binding.tvCityTemp
        val foregroundView = binding.viewForeground
        val backgroundView = binding.viewBackground
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemSavedCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cities = differ.currentList[position]
        bindData(cities, holder)
    }

    private fun bindData(city: City?, holder: Holder) {
        holder.apply {
            cityName.text = city?.name
            countryName.text = city?.country
            temperature.text = ""
            itemView.setOnClickListener { onItemClickListener?.let { it(city!!) } }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
