package com.example.eventfulmb.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.R
import com.example.eventfulmb.module.Alert
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class AlertAdapter(private var alertItems: MutableList<Alert>, private val myApplication: MyApplication) :
    RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private lateinit var context: Context

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val institutionName: TextView = itemView.findViewById(R.id.institutionName)
        val description: TextView = itemView.findViewById(R.id.description)
        val switchBtn: Switch = itemView.findViewById(R.id.switchBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val item = alertItems[position]

        holder.institutionName.text = item.institutionName
        holder.description.text = item.description
        holder.switchBtn.isChecked = item.subscribed

        holder.switchBtn.setOnCheckedChangeListener { _, isChecked ->
            item.subscribed = isChecked

            if (isChecked) {
                val topic = "alert/${item.institutionName}"
                myApplication.subscribeToTopic(topic)
                Toast.makeText(context, "Subscribed to topic: $topic", Toast.LENGTH_SHORT).show()
            } else {
                val topic = "alert/${item.institutionName}"
                myApplication.unsubscribeFromTopic(topic)
                Toast.makeText(context, "Unsubscribed from topic: $topic", Toast.LENGTH_SHORT).show()
            }


            val json = Gson().toJson(alertItems)
            saveJsonToFile(json, context.applicationContext)
        }
    }

    private fun saveJsonToFile(json: String, context: Context) {
        try {
            val fileName = "data_alert.json"
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }

            val filePath = File(context.filesDir, fileName).absolutePath
            Log.d("DistanceActivity", "JSON saved to: $filePath")
        } catch (e: IOException) {
            Log.e("DistanceActivity", "Error saving JSON to file", e)
        }
    }

    private fun readJsonFromFile() {
        try {
            val fileName = "data_alert.json"
            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                val json = file.readText()
                val alertListType = object : TypeToken<List<Alert>>() {}.type
                val alerts = Gson().fromJson<List<Alert>>(json, alertListType)

                alertItems.clear()
                alertItems.addAll(alerts)

                notifyDataSetChanged()
            }
        } catch (e: IOException) {
            Log.e("DistanceActivity", "Error reading JSON from file", e)
        }
    }

    fun updateDataFromJson(context: Context) {
        this.context = context
        readJsonFromFile()
    }

    override fun getItemCount(): Int {
        return alertItems.size
    }
}
