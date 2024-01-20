package com.example.eventfulmb.module

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserLocation (
    var UUID: String = "",
    var hours: Int = 0,
    var minutes: Int = 0,
    var seconds: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {

    override fun toString(): String {
        return "UserLocation(UUID = '$UUID',hours='$hours', minutes='$minutes', seconds='$seconds'')"
    }
}