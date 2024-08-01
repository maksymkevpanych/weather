package com.example.weather.networking

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.weather.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class WeatherNetworkingManager(private val context: Context) {

    private val client = OkHttpClient()

    fun fetchWeatherData(location: String, onSuccess: (JSONObject) -> Unit, onError: (IOException) -> Unit) {
        val url = "${Constants.BASE_URL}forecast.json?key=${Constants.API_KEY}&q=$location&days=7&aqi=no&alerts=no"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Run on the main thread to show Toast
                (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                    Toast.makeText(context, "Error fetching weather data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                onError(e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                        val jsonResponse = JSONObject(responseBody)
                        (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                            onSuccess(jsonResponse)
                        }
                    } catch (e: Exception) {
                        // Run on the main thread to show Toast
                        (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                            Toast.makeText(context, "Error processing weather data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        onError(IOException("Error parsing response body", e))
                    }
                } else {
                    // Run on the main thread to show Toast
                    (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                        Toast.makeText(context, "Unexpected response code: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("WeatherNetworkingManager", "Request URL: $url")
                    onError(IOException("Unexpected code $response"))
                }
            }
        })
    }
}
