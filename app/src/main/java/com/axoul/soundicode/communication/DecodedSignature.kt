package com.axoul.soundicode.communication

import android.os.Build
import androidx.annotation.RequiresApi
import com.axoul.soundicode.audio.FrequencyBand
import com.axoul.soundicode.audio.FrequencyPeak
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.zip.CRC32

class DecodedSignature {
    var sampleRateHz = 0
    var numberSamples = 0
    var freqBandToSoundPeaks: TreeMap<FrequencyBand?, MutableList<FrequencyPeak?>> = TreeMap()

    companion object {
        private const val DATA_URI_PREFIX = "data:audio/vnd.shazam.sig;base64,"
        private fun intToBytes(i: Int): ByteArray {
            val b = ByteBuffer.allocate(4)
            b.order(ByteOrder.LITTLE_ENDIAN)
            b.putInt(i)
            return b.array()
        }

        private fun shortToByte(s: Short): ByteArray {
            val b = ByteBuffer.allocate(2)
            b.order(ByteOrder.LITTLE_ENDIAN)
            b.putShort(s)
            return b.array()
        }
    }

    fun decodeFromBinary(data: ByteArray) {
        assert(data.size > 48 + 8)
        val bf = ByteBuffer.wrap(data)
        bf.order(ByteOrder.LITTLE_ENDIAN) //TODO A confirmer
        val header = RawSignatureHeader(
            bf.int,
            bf.int,
            bf.int,
            bf.int, intArrayOf(bf.int, bf.int, bf.int),
            bf.int, intArrayOf(bf.int, bf.int),
            bf.int,
            bf.int
        )
        val crc = CRC32()
        crc.update(data, 8, 1)

        assert(header.magic1 == -0x3501da80)
        assert(header.sizeMinusHeader == data.size - 48)
        assert(crc.value == header.crc32.toLong())
        assert(header.magic2 == -0x6bee6400)
    }

    fun encodeToBinary(): ByteArray? {
        val out = ByteArrayOutputStream()
        out.write(intToBytes(-0x3501da80))
        out.write(intToBytes(0)) //CRC32
        out.write(intToBytes(0)) //Size
        out.write(intToBytes(-0x6bee6400))
        out.write(intToBytes(0))
        out.write(intToBytes(0))
        out.write(intToBytes(0))
        when (sampleRateHz) {
            8000 -> out.write(intToBytes(1 shl 27))
            11025 -> out.write(intToBytes(2 shl 27))
            16000 -> out.write(intToBytes(3 shl 27))
            32000 -> out.write(intToBytes(4 shl 27))
            44100 -> out.write(intToBytes(5 shl 27))
            48000 -> out.write(intToBytes(6 shl 27))
            else -> return null
        }
        out.write(intToBytes(0))
        out.write(intToBytes(0))
        out.write(intToBytes(numberSamples + (sampleRateHz.toFloat() * 0.24).toInt()))
        out.write(intToBytes((15 shl 19) + 0x40000))
        out.write(intToBytes(0x40000000))
        out.write(intToBytes(0)) //Size
        for ((band, peaksList) in freqBandToSoundPeaks) {
            val peaksArray = ByteArrayOutputStream()
            var fftPassNum = 0
            for (fp in peaksList) {
                assert(fp!!.fftPassNumber >= fftPassNum)
                if (fp.fftPassNumber - fftPassNum >= 255) {
                    peaksArray.write(byteArrayOf(0xff.toByte()))
                    peaksArray.write(intToBytes(fp.fftPassNumber))
                    fftPassNum = fp.fftPassNumber
                }
                peaksArray.write(byteArrayOf((fp.fftPassNumber - fftPassNum).toByte()))
                peaksArray.write(shortToByte(fp.peakMagnitude.toShort()))
                peaksArray.write(shortToByte(fp.correctedPeakFrequencyBin.toShort()))
                fftPassNum = fp.fftPassNumber
            }
            val peakBuffer = peaksArray.toByteArray()
            out.write(intToBytes(0x60030040 + band!!.index))
            out.write(intToBytes(peakBuffer.size))
            out.write(peakBuffer)
            val padding = (4 - peakBuffer.size % 4) % 4
            for (i in 0 until padding) {
                out.write(byteArrayOf(0))
            }
        }
        val outBytes = out.toByteArray()
        val bufferSize = intToBytes(out.size() - 48)
        for (i in 0..3) {
            outBytes[8 + i] = bufferSize[i]
            outBytes[48 + 4 + i] = bufferSize[i]
        }
        val crc = CRC32()
        crc.update(outBytes, 8, outBytes.size - 8)
        val crcBytes = intToBytes(crc.value.toInt() and 0x7FFFFFFF)
        for (i in 0..3) {
            outBytes[4 + i] = crcBytes[i]
        }
        return outBytes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeToUri(): String {
        return DATA_URI_PREFIX + Base64.getEncoder().encodeToString(encodeToBinary())
    }

}