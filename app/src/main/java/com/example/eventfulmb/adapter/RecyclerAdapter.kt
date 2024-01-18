package com.example.eventfulmb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivitySimulationBinding
import com.example.eventfulmb.databinding.CardLayoutBinding


// todo model za simulaciite class RecyclerAdapter(private val data: List<Simulations>,private val binding: CardLayoutBinding) :
class RecyclerAdapter(private val binding: CardLayoutBinding) :

    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val simulation = data[position]
//        holder.itemTitle = simulation.title
//        holder.itemRangeText = simulation.range
//        holder.itemLocationText = simulation.location
//
//        holder.itemView.setOnClickListener{
//            // todo
//        }
    }


    fun updateData() {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented") //return data.size
    }

    inner class ViewHolder(itemView: View, binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(itemView) {
        var itemTitle: TextView
        var itemRangeImage: ImageView
        var itemRangeText: TextView
        var itemHourImage: ImageView
        var itemHourText: TextView
        var itemLocationImage: ImageView
        var itemLocationText: TextView

        init {
            itemTitle = itemView.findViewById(binding.itemTitle.id)
            itemRangeImage = itemView.findViewById(binding.itemRangeImage.id)
            itemRangeText = itemView.findViewById(binding.itemRangeText.id)
            itemHourImage = itemView.findViewById(binding.itemHourImage.id)
            itemHourText = itemView.findViewById(binding.itemRangeText.id)
            itemLocationImage = itemView.findViewById(binding.itemHourImage.id)
            itemLocationText = itemView.findViewById(binding.itemRangeText.id)
        }
    }
}


