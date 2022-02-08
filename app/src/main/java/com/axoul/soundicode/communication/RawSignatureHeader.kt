package com.axoul.soundicode.communication

class RawSignatureHeader(var magic1: Int, var crc32: Int, var sizeMinusHeader: Int, var magic2: Int, var _void1: IntArray, var shiftedSampleRateId: Int, var _void2: IntArray, var numberSamplesPlusDividedSampleRate: Int, var _fixedValue: Int)
