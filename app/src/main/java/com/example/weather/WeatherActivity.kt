package com.example.weather

import TodayForecastAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.viewmodel.WeatherViewModel
import com.example.weather.viewmodel.WeatherViewModelFactory
import com.example.weather.networking.WeatherNetworkingManager
import com.example.weather.networking.WeatherResponse
import com.example.weather.networking.HourlyWeather
import com.google.gson.Gson
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private lateinit var searchField: EditText
    private lateinit var todayForecastRecyclerView: RecyclerView
    private lateinit var threeDaysForecastRecyclerView: RecyclerView
    private lateinit var sevenDaysForecastRecyclerView: RecyclerView
    private lateinit var backgroundImageView: ImageView // New ImageView for background

    private val viewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(application) }
    private lateinit var weatherNetworkingManager: WeatherNetworkingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        initializeUI()

        weatherNetworkingManager = WeatherNetworkingManager(this)


        val latitude = intent.getDoubleExtra("latitude", Double.NaN)
        val longitude = intent.getDoubleExtra("longitude", Double.NaN)

        if (!latitude.isNaN() && !longitude.isNaN()) {

            loadWeatherData("$latitude,$longitude")
        } else {

            Toast.makeText(this, "Unable to get location data", Toast.LENGTH_SHORT).show()
        }

        searchField.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("WeatherActivity", "Search action triggered")
                loadWeatherData(searchField.text.toString())


                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                true
            } else {
                false
            }
        }

        viewModel.weatherData.observe(this) { weatherResponse ->
            updateUIWithWeatherData(weatherResponse)
        }
    }

    private fun initializeUI() {
        searchField = findViewById(R.id.searchField)
        todayForecastRecyclerView = findViewById(R.id.todayForecastRecyclerView)
        threeDaysForecastRecyclerView = findViewById(R.id.threeDaysForecastRecyclerView)
        sevenDaysForecastRecyclerView = findViewById(R.id.sevenDaysForecastRecyclerView)
        backgroundImageView = findViewById(R.id.backgroundImageView) // Initialize background ImageView

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

    private fun updateUIWithWeatherData(weatherResponse: WeatherResponse) {
        todayForecastRecyclerView.adapter =
            TodayForecastAdapter(weatherResponse.forecast.forecastday[0].hour)
        threeDaysForecastRecyclerView.adapter =
            DaysForecastAdapter(weatherResponse.forecast.forecastday.subList(0, 3))
        sevenDaysForecastRecyclerView.adapter =
            DaysForecastAdapter(weatherResponse.forecast.forecastday)


        val currentHourWeather = getCurrentHourWeather(weatherResponse)
        updateBackgroundImage(currentHourWeather.condition.text)
    }


    private fun getCurrentHourWeather(weatherResponse: WeatherResponse): HourlyWeather {
        val currentHour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()


        return weatherResponse.forecast.forecastday[0].hour.firstOrNull {
            SimpleDateFormat("HH", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it.time)).toInt() == currentHour
        } ?: weatherResponse.forecast.forecastday[0].hour[0]
    }


    private fun updateBackgroundImage(condition: String) {
        val imageResource = when {
            condition.contains("sunny", ignoreCase = true) -> R.drawable.sunny_background
            condition.contains("cloudy", ignoreCase = true) -> R.drawable.cloudy_background
            condition.contains("rain", ignoreCase = true) -> R.drawable.rainy_background
            condition.contains("snow", ignoreCase = true) -> R.drawable.snowy_background
            else -> R.drawable.default_background
        }
        backgroundImageView.setImageResource(imageResource)
    }
}
