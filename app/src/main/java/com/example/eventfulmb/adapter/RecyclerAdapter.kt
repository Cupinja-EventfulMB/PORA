package com.example.eventfulmb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfulmb.R
import com.example.eventfulmb.module.SensorData
import com.example.eventfulmb.databinding.CardLayoutBinding

class RecyclerAdapter(
    private val sensorDataList: MutableList<SensorData>,
    private val onItemSubscribedChange: (SensorData, Int) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { sensorData, position ->
            onItemSubscribedChange(sensorData, position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sensorData = sensorDataList[position]
        holder.bind(sensorData, position)
    }

    override fun getItemCount(): Int = sensorDataList.size

    fun updateItem(sensorData: SensorData, position: Int) {
        sensorDataList[position] = sensorData
        notifyItemChanged(position)
    }

    class ViewHolder(
        private val binding: CardLayoutBinding,
        val onSwitchToggle: (SensorData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sensorData: SensorData, position: Int) {
            binding.itemTitle.text = sensorData.category
            binding.itemRangeText.text = "Range: ${sensorData.minVal} - ${sensorData.maxVal}"
            binding.itemLocationText.text = sensorData.location
            binding.itemHourText.text = "Update interval: ${sensorData.hour}:${sensorData.minutes}:${sensorData.seconds}"
            binding.switchBtn.isChecked = sensorData.subscribed
            binding.switchBtn.setOnCheckedChangeListener { _, isChecked ->
                sensorData.subscribed = isChecked
                onSwitchToggle(sensorData, position)
            }
            itemView.setOnClickListener {
            }
        }
    }
}