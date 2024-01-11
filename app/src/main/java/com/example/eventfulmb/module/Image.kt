package com.example.eventfulmb.module

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Image (
    var image: ByteArray,
    var time: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {

    override fun toString(): String {
        return "Message(img='$image', time='$time', latitude='$latitude', longitude='$longitude')"
    }
}