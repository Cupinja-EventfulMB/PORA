package com.example.eventfulmb

import android.app.Application
import android.widget.Toast
import com.example.eventfulmb.module.MqttHandler
import com.example.eventfulmb.module.SensorData
import org.eclipse.paho.android.service.BuildConfig
import org.json.JSONArray
import org.osmdroid.config.Configuration
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MyApplication : Application() {

    private val BROKER_URL = "tcp://192.168.0.102:1883"
    private val CLIENT_ID = "client_id"
    public var mqttHandler: MqttHandler? = null
    val sensorData = mutableListOf<SensorData>()
    private var sensorDataList = mutableListOf<SensorData>()

    override fun onCreate() {
        super.onCreate()
        mqttHandler = MqttHandler(this)
        mqttHandler!!.connect(BROKER_URL, CLIENT_ID)
        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = BuildConfig.APPLICATION_ID
        loadSensorDataFromFile()
    }

    fun publishMessage(topic: String, message: String) {
        Toast.makeText(this, "Publishing message: $message", Toast.LENGTH_SHORT).show()
        mqttHandler!!.publish(topic, message)
    }

    fun sendImage(topic: String, message: ByteArray) {
        Toast.makeText(this, "Publishing message: $message", Toast.LENGTH_SHORT).show()
        mqttHandler!!.send(topic, message)
    }

    fun subscribeToTopic(topic: String) {
        Toast.makeText(this, "Subscribing to topic $topic", Toast.LENGTH_SHORT).show()
        mqttHandler!!.subscribe(topic)
    }

    fun disconnectMqtt() {
        mqttHandler?.disconnect()
    }

    private fun loadSensorDataFromFile() {
        try {
            val file = File(getExternalFilesDir(null), "SensorData.json")
            if (file.exists()) {
                val content = file.readText(Charset.defaultCharset())
                val jsonArray = JSONArray(content)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val sensorDataInstance = SensorData(
                        category = jsonObject.getString("category"),
                        lastUpdate = jsonObject.getString("lastUpdate"),
                        hour = jsonObject.getInt("hour"),
                        minutes = jsonObject.getInt("minutes"),
                        seconds = jsonObject.getInt("seconds"),
                        minVal = jsonObject.getInt("minVal"),
                        maxVal = jsonObject.getInt("maxVal"),
                        location = jsonObject.getString("location"),
                        latitude = jsonObject.getDouble("latitude"),
                        longitude = jsonObject.getDouble("longitude"),
                        subscribed = jsonObject.getBoolean("subscribed")
                    )
                    sensorData.add(sensorDataInstance)
                }
                Toast.makeText(this, "Loaded sensor data from file.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SensorData.json file not found.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load sensor data", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing sensor data", Toast.LENGTH_SHORT).show()
        }
    }
}
