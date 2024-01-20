package com.example.eventfulmb.module

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SensorGeneratedData (
    var category: String = "",
    var value: Int = 0,
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var time: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    ) {
    override fun toString(): String {
        return "SensorData(category-'$category', 'value'='$value', 'location'='$location', 'latitude='$latitude', longitude='$longitude', time='$time')"
    }
}