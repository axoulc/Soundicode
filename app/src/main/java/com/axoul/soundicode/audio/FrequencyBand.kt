package com.axoul.soundicode.audio

enum class FrequencyBand(ind: Int) {
    _250_520(0), _520_1450(1), _1450_3500(2), _3500_5500(3);

    var index = 0

    init {
        index = ind
    }
}