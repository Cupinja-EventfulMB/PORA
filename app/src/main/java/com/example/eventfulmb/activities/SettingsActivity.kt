package com.example.eventfulmb.activities

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.databinding.ActivitySettingsBinding
import com.example.eventfulmb.module.Message
import com.example.eventfulmb.module.UserLocation
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        app = application as MyApplication

        setupSliders()

        binding.saveButton.setOnClickListener {
            saveIntervalsAndFetchLocation()
        }

        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupSliders() {
        val hourSlider = binding.sliderHour
        val minuteSlider = binding.sliderMinutes
        val secondSlider = binding.sliderSeconds

        val hourTextView = binding.txtViewH
        val minuteTextView = binding.txtViewM
        val secondTextView = binding.txtViewS

        hourSlider.addOnChangeListener { slider, value, fromUser ->
            hourTextView.text = value.toInt().toString()
        }

        minuteSlider.addOnChangeListener { slider, value, fromUser ->
            minuteTextView.text = value.toInt().toString()
        }

        secondSlider.addOnChangeListener { slider, value, fromUser ->
            secondTextView.text = value.toInt().toString()
        }
    }
    private fun saveIntervalsAndFetchLocation() {
        val selectedHour = binding.sliderHour.value.toInt()
        val selectedMinute = binding.sliderMinutes.value.toInt()
        val selectedSecond = binding.sliderSeconds.value.toInt()

        // Retrieve UUID from SharedPreferences
        val uuid = sharedPreferences.getString("UUID", "")

        saveIntervalsLocally(selectedHour, selectedMinute, selectedSecond)
        // Continue with existing logic to request location updates
        requestLocationUpdates(selectedHour, selectedMinute, selectedSecond)
    }


    private fun saveIntervalsLocally(hour: Int, minute: Int, second: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("intervalHour", hour)
        editor.putInt("intervalMinute", minute)
        editor.putInt("intervalSecond", second)
        editor.apply()
    }

    private fun requestLocationUpdates(hour: Int, minute: Int, second: Int) {
        val intervalMillis = (hour * 60 * 60 + minute * 60 + second) * 1000L

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates here
                Toast.makeText(
                    this@SettingsActivity,
                    "Location Update: ${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
                val selectedHour = binding.sliderHour.value.toInt()
                val selectedMinute = binding.sliderMinutes.value.toInt()
                val selectedSecond = binding.sliderSeconds.value.toInt()

                val uuid = sharedPreferences.getString("UUID", "")
                val userLocation = UserLocation(
                    UUID = uuid ?: "",
                    hours = selectedHour,
                    minutes = selectedMinute,
                    seconds = selectedSecond,
                    longitude = location.longitude,
                    latitude = location.latitude
                )

                val gson: Gson = GsonBuilder().create()
                val jsonMessage: String = gson.toJson(userLocation)

                val topicToSubscribe = "send/location"
                app.publishMessage(topicToSubscribe, jsonMessage)

            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                intervalMillis,
                0f,  // distance threshold, set to 0 for time-based updates only
                locationListener
            )
        } catch (ex: SecurityException) {
            Toast.makeText(
                this,
                "Location permission not granted. Unable to request location updates.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
// todo gresno uuid dobivam(gresno message strukturo na server) i se updateira sato odma posle save button (lokalno)