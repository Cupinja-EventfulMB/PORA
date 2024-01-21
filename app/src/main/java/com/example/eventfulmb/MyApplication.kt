package com.example.eventfulmb

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.eventfulmb.module.MqttHandler
import com.example.eventfulmb.module.SensorData
import com.example.eventfulmb.module.SensorGeneratedData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.eclipse.paho.android.service.BuildConfig
import org.json.JSONArray
import org.osmdroid.config.Configuration
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Random
import android.content.Context
import android.util.Log
import java.util.UUID

class MyApplication : Application() {

    private val BROKER_URL = "tcp://192.168.0.100:1883"
    private val CLIENT_ID = "client_id"
    public var mqttHandler: MqttHandler? = null
    val sensorData = mutableListOf<SensorData>()
    private val handler = Handler(Looper.getMainLooper())
    private val CHECK_INTERVAL: Long = 1000

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

        val UUID_KEY = "UUID"

        val uuid = sharedPreferences.getString(UUID_KEY, null)

        if (uuid.isNullOrEmpty()) {
            val newUuid = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(UUID_KEY, newUuid).apply()
            Log.d("UUID", "Successfully created UUID: $newUuid")
        } else {
            Log.d("UUID", "Existing UUID: $uuid")
        }

        mqttHandler = MqttHandler(this)
        mqttHandler!!.connect(BROKER_URL, CLIENT_ID)
        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = BuildConfig.APPLICATION_ID
        loadSensorDataFromFile()
        handler.post(checkSensorsTask);
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
        mqttHandler!!.subscribe(topic)
    }

    fun unsubscribeFromTopic(topic: String) {
        mqttHandler!!.unsubscribe(topic)
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


    private val checkSensorsTask: Runnable = object : Runnable {
        override fun run() {
            checkAndUpdateSensors()
            handler.postDelayed(this, CHECK_INTERVAL)
        }
    }

    private fun checkAndUpdateSensors() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        for (sensor in sensorData) {
            val lastUpdateDateTime = LocalDateTime.parse(sensor.lastUpdate, formatter)
            val duration = Duration.between(lastUpdateDateTime, now)

            val hoursDiff = duration.toHours()
            val minutesDiff = duration.toMinutes() % 60
            val secondsDiff = duration.seconds % 60

            if (hoursDiff >= sensor.hour && minutesDiff >= sensor.minutes && secondsDiff >= sensor.seconds && sensor.subscribed) {
                val generatedData = generateSensorData(sensor)
                val gson: Gson = GsonBuilder().create()
                val jsonMessage: String = gson.toJson(generatedData)
                publishMessage("send/sensor", jsonMessage)
                sensor.updateLastUpdateTime()
            }
        }
    }

    private fun generateSensorData(sensor: SensorData): SensorGeneratedData {
        val random = Random()
        val value: Int = sensor.minVal + random.nextInt(sensor.maxVal - sensor.minVal + 1)
        return SensorGeneratedData(
            sensor.category,
            value,
            sensor.location,
            sensor.latitude,
            sensor.longitude,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }

    fun saveSensorDataToFile() {
        try {
            val file = File(getExternalFilesDir(null), "SensorData.json")
            val gson: Gson = GsonBuilder().create()
            val jsonContent: String = gson.toJson(sensorData)
            file.writeText(jsonContent, Charset.defaultCharset())
            Toast.makeText(this, "Saved sensor data to file.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save sensor data", Toast.LENGTH_SHORT).show()
        }
    }


}