package com.axoul.soundicode.communication

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonResponse (
    val matches: List<matches>?,
    val track: track?,
    val timestamp: Long?
)

data class matches (
    val id: String,
    val offset: Float,
    val channel: String?,
    val timeskew: Float,
    val frequencyskew: Float
)

data class track (
    val type: String,
    val title: String,
    val subtitle: String,
    val images: images
)

data class images (
    val background: String,
    val coverart: String,
    val coverarthq: String
)