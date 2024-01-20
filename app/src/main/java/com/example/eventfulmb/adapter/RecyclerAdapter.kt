package com.example.eventfulmb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfulmb.R
import com.example.eventfulmb.module.SensorData
import com.example.eventfulmb.databinding.CardLayoutBinding

class RecyclerAdapter(private val sensorDataList: List<SensorData>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sensorData = sensorDataList[position]
        holder.bind(sensorData)
    }

    override fun getItemCount(): Int = sensorDataList.size

    class ViewHolder(private val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sensorData: SensorData) {
            binding.itemTitle.text = sensorData.category
            binding.itemRangeText.text = "Range: ${sensorData.minVal} - ${sensorData.maxVal}"
            binding.itemLocationText.text = sensorData.location
            binding.itemHourText.text = "Update interval: ${sensorData.hour}:${sensorData.minutes}:${sensorData.seconds}"

            binding.switchBtn.isChecked = sensorData.subscribed
            itemView.setOnClickListener {
                // TODO: Handle the item click event
            }
        }
    }
}