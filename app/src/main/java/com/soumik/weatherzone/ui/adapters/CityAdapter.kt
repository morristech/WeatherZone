package com.soumik.weatherzone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.soumik.weatherzone.data.models.City
import com.soumik.weatherzone.databinding.ItemCitiesBinding
import com.soumik.weatherzone.utils.DiffUtilCallback

class CityAdapter : RecyclerView.Adapter<CityAdapter.Holder>() {
    val differ = AsyncListDiffer(this, DiffUtilCallback())

    inner class Holder(
        private val binding: ItemCitiesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.tvCityName
        val countryName = binding.tvCountryName
        val addBtn = binding.ivAddCity
        val addedTV = binding.tvAdded
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemCitiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cities = differ.currentList[position]
        bindData(holder, cities)
    }

    private fun bindData(holder: Holder, city: City?) {
        holder.apply {
            cityName.text = city?.name
            countryName.text = city?.country
            if (city?.isSaved == 1) {
                addBtn.visibility = View.GONE
                addedTV.visibility = View.VISIBLE
            } else {
                addedTV.visibility = View.GONE
                addBtn.visibility = View.VISIBLE
            }
            addBtn.setOnClickListener {
                onItemClickListener?.let { it(city!!) }
                addedTV.visibility = View.VISIBLE
                addBtn.visibility = View.GONE
            }
            itemView.setOnClickListener {
                onParentItemClickListener?.let { it(city!!) }
            }
        }
    }

    private var onItemClickListener: ((City) -> Unit)? = null
    private var onParentItemClickListener: ((City) -> Unit)? = null

    fun setOnItemClickListener(listener: (City) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnParentClickListener(listener: (City) -> Unit) {
        onParentItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
