package com.axoul.soundicode.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.axoul.soundicode.ui.mic.MicFragment
import com.axoul.soundicode.communication.Communication
import com.axoul.soundicode.communication.JsonResponse
import com.axoul.soundicode.history.History
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

class AudioRecorder(val parent: MicFragment) {
    private val recorderSamplerate = 16000
    private val recorderChannels = AudioFormat.CHANNEL_IN_MONO
    private val recorderAudioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val delayRead: Long = 4000
    private val bufferSize = recorderSamplerate * (delayRead / 1000)

    private lateinit var recorder: AudioRecord
    private var isRecording = false
    private var resultFound = false
    private lateinit var finalResponse: JsonResponse

    private lateinit var loopHandler: Handler

    private val loopTask = object : Runnable {
        override fun run() {
            if (!resultFound and isRecording) {
                Thread { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    writeAudioData()
                }
                }.start()
                loopHandler.postDelayed(this, delayRead)
            } else if (resultFound){
                onResultFound()
            }
        }
    }

    fun startRecorder() {
        initRecorder()
        loopHandler = Handler(Looper.getMainLooper())
        loopHandler.post(loopTask)
    }

    fun stopRecorder() {
        if (recorder.state != AudioRecord.STATE_UNINITIALIZED) {
            recorder.stop()
            recorder.release()
            loopHandler.removeCallbacks(loopTask)
        }
    }

    private fun initRecorder() {
        if (ActivityCompat.checkSelfPermission(
                parent.requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            recorderSamplerate,
            recorderChannels,
            recorderAudioEncoding,
            (2 * bufferSize).toInt()
        )
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            return
        }

        recorder.startRecording()
        isRecording = true
    }

    private fun onResultFound() {
        stopRecorder()
        parent.toogleRecorder()
        parent.addResult(finalResponse)
        val vib = parent.requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vib.vibrate(200)
        }
        History.setHistory(parent.activity, finalResponse)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeAudioData() {
        val data = ShortArray(bufferSize.toInt())
        val size = data.size
        val read = recorder.read(data, 0, size)
        var parser: JsonResponse? = null
        synchronized(this) {
            val sign = Signature()
            val decod = sign.makeSignatureFromBuffer(data)
            if (!resultFound) {
                val communication = Communication()
                val json = communication.recognizeSongFromSignature(decod)
                val moshi = Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(JsonResponse::class.java)
                try {
                    parser = adapter.fromJson(json)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if(parser?.matches?.size!! > 0) {
                    finalResponse = parser as JsonResponse
                    resultFound = true
                }
            }
        }
    }

}