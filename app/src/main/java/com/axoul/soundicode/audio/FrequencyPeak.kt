package com.axoul.soundicode.audio

import kotlin.math.exp
import kotlin.math.sqrt

class FrequencyPeak(var fftPassNumber: Int, var peakMagnitude: Int, var correctedPeakFrequencyBin: Int, private val sampleRateHz: Int) {
    val frequencyHz: Float
        get() = correctedPeakFrequencyBin.toFloat() * (sampleRateHz.toFloat() / 2 / 1024 / 64)
    val amplitudePcm: Float
        get() = (sqrt(exp((peakMagnitude.toFloat() - 6144) / 1477.3) * (1 shl 17).toFloat() / 2) / 1024).toFloat()
    val seconds: Float
        get() = fftPassNumber.toFloat() * 128 / sampleRateHz.toFloat()
}