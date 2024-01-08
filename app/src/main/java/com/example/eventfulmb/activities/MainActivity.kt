package com.example.eventfulmb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivityMainBinding
import com.example.eventfulmb.module.MqttHandler

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val BROKER_URL = "tcp://192.168.1.120:1883"
    private val CLIENT_ID = "client_id"
    private var mqttHandler: MqttHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mqttHandler = MqttHandler(this)
        mqttHandler!!.connect(BROKER_URL, CLIENT_ID)

        val topicToSubscribe = "your/topic"
        subscribeToTopic(topicToSubscribe)

        binding.simulationActivityButton.setOnClickListener {
            val simulationIntent= Intent(this,SimulationActivity::class.java)
            startActivity(simulationIntent)
        }

        binding.settingsActivityButton.setOnClickListener {
            val settingsIntent= Intent(this,SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
        binding.fabCamera.setOnClickListener{
            val cameraIntent = Intent(this,CameraActivity::class.java)
            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show()
            startActivity(cameraIntent)
        }

        binding.messageButton.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        // Exit application
        val exitButton = binding.exitBtn
        exitButton.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to exit the application?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes"){ _,_ -> finish()}
                    .create().show()
        }
    }

    override fun onDestroy() {
        mqttHandler!!.disconnect();
        super.onDestroy()
    }

    private fun publishMessage(topic: String, message: String) {
        Toast.makeText(this, "Publishing message: $message", Toast.LENGTH_SHORT).show()
        mqttHandler!!.publish(topic, message)
    }

    private fun subscribeToTopic(topic: String) {
        Toast.makeText(this, "Subscribing to topic $topic", Toast.LENGTH_SHORT).show()
        mqttHandler!!.subscribe(topic)
    }
}