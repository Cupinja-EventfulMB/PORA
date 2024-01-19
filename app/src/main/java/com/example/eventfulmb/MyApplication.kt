package com.example.eventfulmb

import android.app.Application
import android.widget.Toast
import com.example.eventfulmb.module.MqttHandler
import org.eclipse.paho.android.service.BuildConfig
import org.osmdroid.config.Configuration

class MyApplication : Application() {

    private val BROKER_URL = "tcp://192.168.0.102:1883"
    private val CLIENT_ID = "client_id"
    public var mqttHandler: MqttHandler? = null

    override fun onCreate() {
        super.onCreate()
        mqttHandler = MqttHandler(this)
        mqttHandler!!.connect(BROKER_URL, CLIENT_ID)
        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = BuildConfig.APPLICATION_ID
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
