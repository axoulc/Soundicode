package com.axoul.soundicode.audio

import com.axoul.soundicode.utils.ComplexNumber
import com.axoul.soundicode.communication.DecodedSignature
import org.jtransforms.fft.FloatFFT_1D
import java.util.*
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

class Signature {
    private val ringBufferOfSamples: ShortArray = ShortArray(2048)
    private var ringBufferOfSamplesIndex: Int = 0
    private val reorderedRingBufferOfSamples: FloatArray = FloatArray(2048)
    private val fftOutputs: Array<FloatArray> = Array(256) { FloatArray(1024) }
    private var fftOutputsIndex: Int = 0
    private val fftObject: FloatFFT_1D = FloatFFT_1D(2048)
    private val spreadFftOutputs: Array<FloatArray> = Array(256) { FloatArray(1024) }
    private var spreadFftOutputsIndex: Int = 0
    private var numSpreadFftsDone: Int = 0
    private val signature: DecodedSignature = DecodedSignature()

    init {
        signature.sampleRateHz = 16000
        signature.numberSamples = 0
    }

    companion object {
        private val hanningWindow = hanningWindow(2048)

        private fun chunkSort(inShort: ShortArray, chunkSize: Int): Array<ShortArray?> {
            val rest = inShort.size % chunkSize
            val chunks = inShort.size / chunkSize + if (rest > 0) 1 else 0
            val arrays = arrayOfNulls<ShortArray>(chunks)
            for (i in 0 until if (rest > 0) chunks - 1 else chunks) {
                arrays[i] = inShort.copyOfRange(i * chunkSize, i * chunkSize + chunkSize)
            }
            if (rest > 0) {
                arrays[chunks - 1] =
                    inShort.copyOfRange((chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest)
            }
            return arrays
        }

        private fun hanningWindow(size: Int): FloatArray {
            val sizeWithZeros = size + 2
            val hanningArrayWithZeros = FloatArray(sizeWithZeros)
            var m = 1 - sizeWithZeros
            var i = 0
            while (m < sizeWithZeros) {
                hanningArrayWithZeros[i] =
                    (0.5 + 0.5 * cos(Math.PI * m / (sizeWithZeros - 1))).toFloat()
                m += 2
                i++
            }
            return hanningArrayWithZeros.copyOfRange(1, size + 1)
        }

        private fun toComplexNumber(inJtrans: FloatArray): Array<ComplexNumber?> {
            val complexType = arrayOfNulls<ComplexNumber>(inJtrans.size / 2)
            for (s in complexType.indices) {
                complexType[s] =
                    ComplexNumber(inJtrans[s * 2].toDouble(), inJtrans[s * 2 + 1].toDouble())
            }
            return complexType
        }

    }

    fun makeSignatureFromBuffer(data: ShortArray): DecodedSignature {
        signature.numberSamples = data.size
        for (chunk in chunkSort(data, 128)) {
            doFft(chunk)
            doPeakSpreading()
            numSpreadFftsDone++
            if (numSpreadFftsDone >= 40) {
                doPeakRecognition()
            }
        }
        return signature
    }

    private fun doFft(chunk: ShortArray?) {
        System.arraycopy(chunk, 0, ringBufferOfSamples, ringBufferOfSamplesIndex, chunk!!.size)
        ringBufferOfSamplesIndex += 128
        ringBufferOfSamplesIndex = ringBufferOfSamplesIndex and 2047
        for (i in 0..2047) {
            reorderedRingBufferOfSamples[i] =
                ringBufferOfSamples[i + ringBufferOfSamplesIndex and 2047].toFloat() * hanningWindow[i]
        }
        val fftBuffer = reorderedRingBufferOfSamples.clone()
        fftObject.realForward(fftBuffer)
        val complexFftResults = toComplexNumber(fftBuffer)
        val bufferMut = fftOutputs[fftOutputsIndex]
        for (i in 0..1023) {
            bufferMut[i] = max(
                ((complexFftResults[i]?.re!!).pow(2.0) + (complexFftResults[i]?.im!!).pow(
                    2.0
                )) / (1 shl 17), 0.0000000001
            ).toFloat()
        }
        fftOutputsIndex++
        fftOutputsIndex = fftOutputsIndex and 255
    }

    private fun doPeakSpreading() {
        val realFftResults = fftOutputs[(fftOutputsIndex - 1) and 255]
        val spreadFftResults = realFftResults.clone()
        for (position in 0..1021) {
            spreadFftResults[position] = max(
                spreadFftResults[position],
                max(spreadFftResults[position + 1], spreadFftResults[position + 2])
            )
        }
        val spreadFftResultsCpy = spreadFftResults.clone()
        for (position in 0..1023) {
            for (formerFftNumber in intArrayOf(1, 3, 6)) {
                val formerFftOutput =
                    spreadFftOutputs[(spreadFftOutputsIndex - formerFftNumber) and 255]
                formerFftOutput[position] =
                    max(formerFftOutput[position], spreadFftResultsCpy[position])
            }
        }
        spreadFftOutputsIndex++
        spreadFftOutputsIndex = spreadFftOutputsIndex and 255
    }

    private fun doPeakRecognition() {
        val fftMinus46 = fftOutputs[(fftOutputsIndex - 46) and 255]
        val fftMinus49 = spreadFftOutputs[(spreadFftOutputsIndex - 49) and 255]


        for (binPosition in 10..1013) {
            if (fftMinus46[binPosition] >= 1.0 / 64.0 && fftMinus46[binPosition] >= fftMinus49[binPosition - 1]) {
                var maxNeighborInFftMinus49 = 0f
                for (neighborOffset in intArrayOf(-10, -7, -4, -3, 1, 2, 5, 8)) {
                    maxNeighborInFftMinus49 =
                        max(maxNeighborInFftMinus49, fftMinus49[binPosition + neighborOffset])
                }
                if (fftMinus46[binPosition] > maxNeighborInFftMinus49) {
                    var maxNeighborInOtherAdjacentFfts = maxNeighborInFftMinus49
                    for (otherOffset in intArrayOf(-53, -45, 165, 172, 179, 186, 193, 200, 214, 221, 228, 235, 242, 249)) {
                        val otherFft = spreadFftOutputs[spreadFftOutputsIndex + otherOffset and 255]
                        maxNeighborInOtherAdjacentFfts =
                            max(maxNeighborInOtherAdjacentFfts, otherFft[binPosition - 1])
                    }
                    if (fftMinus46[binPosition] > maxNeighborInOtherAdjacentFfts) {
                        val fftPassNum = numSpreadFftsDone - 46
                        val peakMagnitude = (max(
                            ln(fftMinus46[binPosition].toDouble()),
                            1.0 / 64.0
                        ) * 1477.3 + 6144.0).toFloat()
                        val peakMagnitudeBefore = (max(
                            ln(fftMinus46[binPosition - 1].toDouble()),
                            1.0 / 64.0
                        ) * 1477.3 + 6144.0).toFloat()
                        val peakMagnitudeAfter = (max(
                            ln(fftMinus46[binPosition + 1].toDouble()),
                            1.0 / 64.0
                        ) * 1477.3 + 6144.0).toFloat()
                        val peakVariation1 =
                            (peakMagnitude * 2.0 - peakMagnitudeBefore - peakMagnitudeAfter).toFloat()
                        val peakVariation2 =
                            ((peakMagnitudeAfter - peakMagnitudeBefore) * 32.0 / peakVariation1).toFloat()
                        val correctedPeakFrequencyBin =
                            (binPosition * 64 + peakVariation2.toInt().toShort()).toShort()
                        //assert (peakVariation1 > 0);
                        val frequencyHz =
                            (correctedPeakFrequencyBin.toFloat() * (16000.0 / 2.0 / 1024.0 / 64.0)).toFloat()
                        var band: FrequencyBand?
                        band = if (frequencyHz < 250) {
                            continue
                        } else if (frequencyHz < 520) {
                            FrequencyBand._250_520
                        } else if (frequencyHz < 1450) {
                            FrequencyBand._520_1450
                        } else if (frequencyHz < 3500) {
                            FrequencyBand._1450_3500
                        } else if (frequencyHz < 5500) {
                            FrequencyBand._3500_5500
                        } else {
                            continue
                        }
                        if (!signature.freqBandToSoundPeaks.containsKey(band)) {
                            signature.freqBandToSoundPeaks[band] = ArrayList()
                        }
                        signature.freqBandToSoundPeaks[band]!!.add(
                            FrequencyPeak(
                                fftPassNum,
                                peakMagnitude.toInt(),
                                correctedPeakFrequencyBin.toInt(),
                                16000
                            )
                        )
                    }
                }
            }
        }
    }
}