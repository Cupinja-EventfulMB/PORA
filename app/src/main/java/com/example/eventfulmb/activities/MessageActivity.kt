package com.example.eventfulmb.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.databinding.ActivityMessageBinding
import com.example.eventfulmb.module.Message
import com.example.eventfulmb.module.MqttHandler
import com.google.android.gms.location.*
import com.google.gson.Gson
import java.time.LocalDateTime
import com.google.gson.GsonBuilder
import java.time.format.DateTimeFormatter

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var app: MyApplication

    // Location variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 42
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication


        binding.messageInput.setEndIconOnClickListener(View.OnClickListener {
            binding.messageInput.editText?.text = null
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        binding.saveButton.setOnClickListener {
            val messageBody = binding.messageInput.editText?.text.toString()
            val messageCategory = binding.menu.editText?.text.toString()

            if (messageBody.isNotEmpty()) {
                saveMessageWithLocation(messageBody, messageCategory)
            } else {
                Log.d("MessageActivity", "Message body is empty. Cannot save.")
            }
        }

    }


    private fun saveMessageWithLocation(messageBody: String, messageCategory: String) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0

                    val message = Message(
                        body = messageBody,
                        category = messageCategory,
                        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        latitude = latitude,
                        longitude = longitude
                    )

                    val gson: Gson = GsonBuilder().create()
                    val jsonMessage: String = gson.toJson(message)

                    Log.d("MessageActivity", "Saved Message: $message")
                    val topicToSubscribe = "send/message"
                    app.publishMessage(topicToSubscribe, jsonMessage)

                    binding.messageInput.editText?.text = null
                    binding.menu.editText?.text = null
                    binding.menu.clearFocus()
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }


/*
    override fun onDestroy() {
        (application as MyApplication).disconnectMqtt()
        super.onDestroy()
    }
*/

}