package com.axoul.soundicode.ui.mic

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.axoul.soundicode.databinding.FragmentMicBinding
import com.axoul.soundicode.audio.AudioRecorder
import com.axoul.soundicode.R
import com.axoul.soundicode.communication.JsonResponse
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MicFragment : Fragment() {
    private lateinit var binding: FragmentMicBinding
    private lateinit var musicContainer: LinearLayout
    private lateinit var musicTitle: TextView
    private lateinit var musicArtist: TextView
    private lateinit var musicImg: ImageView
    private lateinit var fabRecord: FloatingActionButton
    private var recording = false
    private lateinit var recorder: AudioRecorder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMicBinding.inflate(inflater, container, false)
        val root = binding.root
        musicContainer = binding.musicContainer
        musicTitle = binding.musicTitle
        musicArtist = binding.musicArtist
        musicImg = binding.musicImg
        musicContainer.isEnabled = false
        fabRecord = binding.recordBtn
        fabRecord.setColorFilter(Color.WHITE)
        fabRecord.setOnClickListener { toogleRecorder() }
        return root
    }

    fun toogleRecorder() {
        if (recording) {
            fabRecord.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red_record))
            fabRecord.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24)
            fabRecord.setColorFilter(Color.WHITE)
            recorder.stopRecorder()
            recording = false
        } else {
            fabRecord.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.black))
            fabRecord.setImageResource(R.drawable.ic_baseline_stop_24)
            fabRecord.setColorFilter(Color.WHITE)
            recorder = AudioRecorder(this)
            recorder.startRecorder()
            recording = true
        }
    }

    fun addResult(rsp: JsonResponse?) {
        musicContainer.isEnabled = true
        val title = rsp?.track?.title
        val artist = rsp?.track?.subtitle
        musicTitle.text = title
        musicArtist.text = artist
        if (rsp != null) {
            Glide.with(this).load(rsp.track.images.coverart).into(musicImg)
        }
    }
}