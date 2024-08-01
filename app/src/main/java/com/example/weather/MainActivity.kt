package com.example.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weather.R
import com.example.weather.WeatherActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermissionsAndStartApp()
    }

    private fun checkLocationPermissionsAndStartApp() {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted || !coarseLocationGranted) {
            requestPermissionsLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            startWeatherActivity()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted && coarseLocationGranted) {
            startWeatherActivity()
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Location permissions are needed to show weather for your current location",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Grant") {
                checkLocationPermissionsAndStartApp()
            }.show()
        }
    }

    private fun startWeatherActivity() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissionsLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            return
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val latitude: Double
            val longitude: Double

            if (location != null) {

                latitude = location.latitude
                longitude = location.longitude
            } else {

                latitude = 48.6200
                longitude = 22.2870
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Unable to retrieve location. Showing weather for Uzhhorod.",
                    Snackbar.LENGTH_LONG
                ).show()
            }


            val intent = Intent(this, WeatherActivity::class.java).apply {
                putExtra("latitude", latitude)
                putExtra("longitude", longitude)
            }
            startActivity(intent)
            finish()
        }
    }
}
