// File: WeatherActivity.kt
package com.example.weather

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.viewmodel.WeatherViewModel
import com.example.weather.viewmodel.WeatherViewModelFactory
import com.example.weather.networking.WeatherNetworkingManager
import com.example.weather.networking.WeatherResponse
import com.google.gson.Gson
import org.json.JSONObject

class WeatherActivity : AppCompatActivity() {

    private lateinit var searchField: EditText
    private lateinit var todayForecastRecyclerView: RecyclerView
    private lateinit var threeDaysForecastRecyclerView: RecyclerView
    private lateinit var sevenDaysForecastRecyclerView: RecyclerView

    private val viewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(application) }
    private lateinit var weatherNetworkingManager: WeatherNetworkingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        initializeUI()

        weatherNetworkingManager = WeatherNetworkingManager(this)

        searchField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("WeatherActivity", "Search action triggered")
                loadWeatherData(searchField.text.toString())
                true
            } else {
                false
            }
        }

        viewModel.weatherData.observe(this) { weatherResponse ->
            todayForecastRecyclerView.adapter =
                TodayForecastAdapter(weatherResponse.forecast.forecastday[0].hour)
            threeDaysForecastRecyclerView.adapter =
                DaysForecastAdapter(weatherResponse.forecast.forecastday.subList(0, 3))
            sevenDaysForecastRecyclerView.adapter =
                DaysForecastAdapter(weatherResponse.forecast.forecastday)
        }
    }

    private fun initializeUI() {
        searchField = findViewById(R.id.searchField)
        todayForecastRecyclerView = findViewById(R.id.todayForecastRecyclerView)
        threeDaysForecastRecyclerView = findViewById(R.id.threeDaysForecastRecyclerView)
        sevenDaysForecastRecyclerView = findViewById(R.id.sevenDaysForecastRecyclerView)

        todayForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        threeDaysForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sevenDaysForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadWeatherData(location: String) {
        weatherNetworkingManager.fetchWeatherData(location, { jsonResponse ->
            try {
                val weatherResponse = convertJsonToWeatherResponse(jsonResponse)
                viewModel.updateWeatherData(weatherResponse)
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error processing weather data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }, { error ->
            runOnUiThread {

                Toast.makeText(this, "Error fetching weather data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun convertJsonToWeatherResponse(response: JSONObject): WeatherResponse {
        val gson = Gson()
        val jsonResponse = response.toString()
        return gson.fromJson(jsonResponse, WeatherResponse::class.java)
    }
}
