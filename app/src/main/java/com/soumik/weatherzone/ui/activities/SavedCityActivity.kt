package com.soumik.weatherzone.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.soumik.weatherzone.R
import com.soumik.weatherzone.data.models.CityUpdate
import com.soumik.weatherzone.data.repository.local.CityRepository
import com.soumik.weatherzone.databinding.ActivitySavedCityBinding
import com.soumik.weatherzone.db.WeatherDatabase
import com.soumik.weatherzone.ui.adapters.SavedCityAdapter
import com.soumik.weatherzone.utils.RecyclerItemTouchHelper
import com.soumik.weatherzone.utils.lightStatusBar
import com.soumik.weatherzone.viewmodel.CityViewModel

class SavedCityActivity :
    AppCompatActivity(),
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private lateinit var binding: ActivitySavedCityBinding

    private lateinit var viewModel: CityViewModel
    private lateinit var repository: CityRepository
    private lateinit var mAdapter: SavedCityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this@SavedCityActivity, true)

        binding = ActivitySavedCityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this@SavedCityActivity).get(CityViewModel::class.java)
        repository = CityRepository(WeatherDatabase.getDatabase(this@SavedCityActivity)!!)
        mAdapter = SavedCityAdapter()

        setUpRecyclerView()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedCities(repository, 1)
    }

    private fun setUpObservers() {
        viewModel.savedCities.observe(
            this@SavedCityActivity,
            { cities ->
                mAdapter.differ.submitList(cities)
            }
        )
    }

    private fun setUpRecyclerView() {
        binding.rvSavedCity.apply {
            layoutManager = LinearLayoutManager(this@SavedCityActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }

        ItemTouchHelper(RecyclerItemTouchHelper(this@SavedCityActivity)).attachToRecyclerView(
            binding.rvSavedCity
        )

        mAdapter.setOnItemClickListener {
            startActivity(
                Intent(
                    this@SavedCityActivity,
                    WeatherDetailsActivity::class.java
                ).putExtra(WeatherDetailsActivity.CITY_ID, it.id.toString())
            )
        }
    }

    fun onSearchTextClicked(view: View) {
        val intent = Intent(this@SavedCityActivity, SearchActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this@SavedCityActivity,
            Pair.create(binding.tvCitySearch, getString(R.string.label_search_hint))
        )
        startActivity(intent, options.toBundle())
        Handler(Looper.myLooper()!!).postDelayed({ finish() }, 1000)
    }

    fun onBackButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is SavedCityAdapter.Holder) {
            val pos = viewHolder.adapterPosition
            val cities = mAdapter.differ.currentList[pos]
            viewModel.updateSavedCities(
                CityRepository(WeatherDatabase.getDatabase(this@SavedCityActivity)!!),
                CityUpdate(cities.id, 0)
            )

            Snackbar.make(binding.clParent, "City removed from saved items", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        viewModel.updateSavedCities(
                            CityRepository(WeatherDatabase.getDatabase(this@SavedCityActivity)!!),
                            CityUpdate(cities.id, 1)
                        )
                    }
                    setBackgroundTint(resources.getColor(R.color.colorPrimary))
                    setActionTextColor(resources.getColor(R.color.color_grey))
                    show()
                }
        }
    }
}
