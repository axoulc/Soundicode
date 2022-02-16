package com.axoul.soundicode.ui.mic

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.axoul.soundicode.databinding.FragmentMicBinding
import com.axoul.soundicode.audio.AudioRecorder
import com.axoul.soundicode.R
import com.axoul.soundicode.communication.JsonResponse
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator

class MicFragment : Fragment() {
    private lateinit var binding: FragmentMicBinding
    private lateinit var musicTitle: TextView
    private lateinit var musicArtist: TextView
    private lateinit var musicImg: ImageView
    private lateinit var recBtn: Button
    private lateinit var indic: CircularProgressIndicator
    private var recording = false
    private lateinit var recorder: AudioRecorder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMicBinding.inflate(inflater, container, false)
        val root = binding.root
        musicTitle = binding.musicTitle
        musicArtist = binding.musicArtist
        musicImg = binding.musicImg
        indic = binding.indic
        musicTitle.isEnabled = false
        musicArtist.isEnabled = false
        musicImg.isEnabled = false
        indic.isVisible = false
        recBtn = binding.recordBtn
        recBtn.setOnClickListener { toogleRecorder() }
        return root
    }

    fun toogleRecorder() {
        if (recording) {
            indic.isVisible = false
            recBtn.text = resources.getString(R.string.start_recording)
            recorder.stopRecorder()
            recording = false
        } else {
            indic.isVisible = true
            recBtn.text = resources.getString(R.string.stop_recording)
            recorder = AudioRecorder(this)
            recorder.startRecorder()
            recording = true
        }
    }

    fun addResult(rsp: JsonResponse?) {
        musicTitle.isEnabled = true
        musicArtist.isEnabled = true
        musicImg.isEnabled = true
        val title = rsp?.track?.title
        val artist = rsp?.track?.subtitle
        musicTitle.text = title
        musicArtist.text = artist
        if (rsp != null) {
            Glide.with(this).load(rsp.track?.images?.coverart).into(musicImg)
        }
    }
}