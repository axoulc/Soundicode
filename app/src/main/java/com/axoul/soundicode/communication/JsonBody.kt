package com.axoul.soundicode.communication

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonBody(val _samplems: Int, val _uri: String?) {
    var geolocation: geolocation
    var signature: signature
    var timestamp: Long
    var timezone: String

    init {
        val timestampMs = System.currentTimeMillis()
        geolocation = geolocation()
        geolocation.altitude = 350
        geolocation.latitude = 45
        geolocation.longitude = 2
        signature = signature()
        signature.samplems = _samplems
        signature.timestamp = timestampMs
        signature.uri = _uri
        timestamp = timestampMs
        timezone = "Europe/Paris"
    }
}

class geolocation {
    var altitude = 0
    var latitude = 0
    var longitude = 0
}

class signature {
    var samplems = 0
    var timestamp: Long = 0
    var uri: String? = null
}