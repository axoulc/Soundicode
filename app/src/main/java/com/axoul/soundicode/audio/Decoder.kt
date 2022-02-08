package com.axoul.soundicode.audio

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

class Decoder(private val fileDescriptor: FileDescriptor) {
    private lateinit var mediaExtractor: MediaExtractor
    private lateinit var mediaCodec: MediaCodec
    private var trackFormat: MediaFormat? = null
    private var isFinish = false
    private var maxBufferSize: Int = 100*1000
    private var sampleRate: Int = 0
    private val finalSampleRate = 16000
    private val timeRecord = 4

    private lateinit var byteArray: ByteArrayOutputStream

    fun startDecoder() {
        initDecoder()
        Thread{ decode() }.start()
    }

    private fun initDecoder() {
        try {
            mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(fileDescriptor)
            var audioTrackIndex = 1
            var mime: String? = null
            trackFormat = null
            for (i in 0 until mediaExtractor.trackCount) {
                trackFormat = mediaExtractor.getTrackFormat(i)
                mime = trackFormat!!.getString(MediaFormat.KEY_MIME)
                if (mime!!.startsWith("audio")) {
                    audioTrackIndex = i;
                    break
                }
            }
            if (audioTrackIndex == -1) {
                return
            }
            maxBufferSize = if(trackFormat!!.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                trackFormat!!.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            } else {
                100 * 1000
            }

            sampleRate = trackFormat!!.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            mediaExtractor.selectTrack(audioTrackIndex)
            mediaCodec = MediaCodec.createDecoderByType(mime!!)
            mediaCodec.configure(trackFormat, null, null, 0)
            mediaCodec.start()



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun decode() {
        byteArray = ByteArrayOutputStream()
        val bufferInfo = MediaCodec.BufferInfo()
        val buffer = ByteBuffer.allocate(maxBufferSize)
        var outputBufferIndex = -1
        while (true) {
            val TIMEOUT_US = (100 * 1000).toLong()
            val inputIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_US)
            if (inputIndex >= 0) {
                val sampleTimeUs = mediaExtractor.sampleTime
                if (sampleTimeUs == -1L) {
                    break
                }
                bufferInfo.size = mediaExtractor.readSampleData(buffer, 0)
                bufferInfo.presentationTimeUs = sampleTimeUs
                bufferInfo.flags = mediaExtractor.sampleFlags
                val content = ByteArray(buffer.remaining())
                buffer.get(content)
                val inputBuffer = mediaCodec.getInputBuffer(inputIndex)
                inputBuffer!!.put(content)
                mediaCodec.queueInputBuffer(inputIndex, 0, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags)
                mediaExtractor.advance()
            }
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
            while (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                val out = ByteArray(outputBuffer!!.remaining())
                outputBuffer.get(out)
                try {
                    byteArray.write(out)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
            }
        }
        mediaExtractor.release()
        mediaCodec.stop()
        mediaCodec.release()
    }

    val shortData: ShortArray
    ?   get() {
        if (byteArray.size() == 0) {
            return null
        }

        //Bytes to short
        val bytes = byteArray.toByteArray()
        val bf = ByteBuffer.wrap(bytes)
        bf.order(ByteOrder.nativeOrder())
        val stereo = ShortArray(bytes.size / 2)
        for (i in stereo.indices) {
            stereo[i] = bf.short
        }

        //Stereo to mono
        val monoAvg = ShortArray(stereo.size / 2)
        for (i in monoAvg.indices) {
            monoAvg[i] = ((stereo[i * 2] + stereo[i * 2 + 1]) / 2).toShort()
        }

        //
        //final int noSamples = sampleRate * timeRecord;
        //short[] fullSR = Arrays.copyOfRange(monoAvg, monoAvg.length - noSamples/2, monoAvg.length + noSamples/2 + 1);
        if (sampleRate == finalSampleRate) {
            return monoAvg
        }
        val sampleRatio = (sampleRate / finalSampleRate).toFloat()
        val newLength = (monoAvg.size / sampleRatio).roundToInt()
        val result = ShortArray(newLength)
        var offsetResult = 0
        var offsetBuffer = 0
        while (offsetResult < result.size) {
            val nextOffsetBuffer = ((offsetResult + 1) * sampleRatio).roundToInt()
            var accum = 0
            var count = 0
            var i = offsetBuffer
            while (i < nextOffsetBuffer && i < monoAvg.size) {
                accum += monoAvg[i]
                count++
                i++
            }
            result[offsetResult] = (accum / count).toShort()
            offsetResult++
            offsetBuffer = nextOffsetBuffer
        }
        return result
    }
}