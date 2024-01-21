package com.example.eventfulmb.module

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.Closeable

class MqttHandler(private var context: Context) : Closeable, MqttCallback {

    private var client: MqttClient? = null

    fun changeContext(context: Context) {
        this.context = context
    }

    fun connect(brokerUrl: String?, clientId: String?) {
        try {
            val persistence = MemoryPersistence()

            client = MqttClient(brokerUrl, clientId, persistence)

            val connectOptions = MqttConnectOptions()
            connectOptions.isCleanSession = true
            connectOptions.maxReconnectDelay = 60
            connectOptions.isAutomaticReconnect = true

            client!!.connect(connectOptions)
            client!!.setCallback(this)
        } catch (e: MqttException) {
            e.message?.let { Log.d("ERROR", it) };
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            client?.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String?, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            client?.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun send(topic: String?, byteArray: ByteArray) {
        try {
            val mqttMessage = MqttMessage(byteArray)
            client?.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


    fun subscribe(topic: String?) {
        try {
            client?.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String?) {
        try {
            client?.unsubscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val receivedMessage = message?.payload?.toString(Charsets.UTF_8)
        receivedMessage?.let {
            Log.d("MqttHandler", "Received message: $it")
            Log.d("Context mqtt", context.toString())
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
        try {
            Log.d("MqttHandler", "Delivery complete for message: ")
        } catch (e: MqttException) {
            Log.e("MqttHandler", "Error in deliveryComplete", e)
        }
    }


    override fun connectionLost(cause: Throwable?) {
        Log.d("MqttHandler", "Connection lost: ${cause?.message}")
    }


    override fun close() {
        disconnect()
    }
}