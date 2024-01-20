package com.example.eventfulmb.module

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SensorData (
    var category: String = "",
    var lastUpdate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    var hour: Int = 0,
    var minutes: Int = 0,
    var seconds: Int = 0,
    var minVal: Int = 0,
    var maxVal: Int = 0,
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var subscribed: Boolean = false,
) {

    override fun toString(): String {
        return "SensorData(category-'$category', time='$lastUpdate', interval='$hour:$minutes:$seconds', 'minVal'='$minVal', 'maxVal'='$maxVal', 'location'='$location', 'latitude='$latitude', longitude='$longitude', subscribed='$subscribed')"
    }
}