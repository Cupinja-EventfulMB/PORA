package com.example.eventfulmb.module

import java.time.LocalDateTime

class Message(
    var body: String = "",
    var category: String = "",
    var time: LocalDateTime = LocalDateTime.now(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {

    override fun toString(): String {
        return "Message(body='$body', theme='$category', time='$time', latitude='$latitude', longitude='$longitude')"
    }
}
