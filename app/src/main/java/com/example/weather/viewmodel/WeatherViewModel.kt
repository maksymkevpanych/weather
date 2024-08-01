package com.example.weather.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.networking.WeatherResponse

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    fun updateWeatherData(weatherResponse: WeatherResponse) {
        _weatherData.value = weatherResponse
    }
}
