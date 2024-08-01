package com.example.weather.networking

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

data class CurrentWeather(
    val temp_c: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<HourlyWeather>
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

data class HourlyWeather(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)
