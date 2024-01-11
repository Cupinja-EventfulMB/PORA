package com.example.eventfulmb

import android.app.Application
import android.widget.Toast
import com.example.eventfulmb.module.MqttHandler

class MyApplication : Application() {

    private val BROKER_URL = "tcp://192.168.1.220:1883"
    private val CLIENT_ID = "client_id"
    private var mqttHandler: MqttHandler? = null

    override fun onCreate() {
        super.onCreate()

        mqttHandler = MqttHandler(this)
        mqttHandler!!.connect(BROKER_URL, CLIENT_ID)
    }

    fun publishMessage(topic: String, message: String) {
        Toast.makeText(this, "Publishing message: $message", Toast.LENGTH_SHORT).show()
        mqttHandler!!.publish(topic, message)
    }

    fun subscribeToTopic(topic: String) {
        Toast.makeText(this, "Subscribing to topic $topic", Toast.LENGTH_SHORT).show()
        mqttHandler!!.subscribe(topic)
    }

    fun disconnectMqtt() {
        mqttHandler?.disconnect()
    }
}
