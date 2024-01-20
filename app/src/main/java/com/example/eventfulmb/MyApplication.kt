package com.example.eventfulmb

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.eventfulmb.module.MqttHandler
import java.util.UUID

class MyApplication : Application() {

    private val BROKER_URL = "tcp://10.104.1.143:1883"
    private val CLIENT_ID = "client_id"
    public var mqttHandler: MqttHandler? = null

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

        val UUID_KEY = "UUID" // /data/data/com.example.eventfulmb/shared_prefs/myApp.xml

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
}
