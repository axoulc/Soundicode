package com.axoul.soundicode.history

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.axoul.soundicode.R
import com.axoul.soundicode.communication.JsonResponse
import com.bumptech.glide.Glide

class HistoryDialog(private val mContext: Context, private val detailResponse: JsonResponse) : Dialog(mContext) {
    private lateinit var dialogImg: ImageView
    private lateinit var dialogTitle: TextView
    private lateinit var dialogArtist: TextView
    private lateinit var closeBtn: Button

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.history_dialog)
        dialogImg = findViewById(R.id.dialog_img)
        dialogTitle = findViewById(R.id.dialog_title)
        dialogArtist = findViewById(R.id.dialog_artist)
        closeBtn = findViewById(R.id.close_btn)
        closeBtn.setOnClickListener { dismiss() }
        Glide.with(mContext).load(detailResponse.track.images.coverart).into(dialogImg)
        dialogTitle.text = detailResponse.track.title
        dialogArtist.text = detailResponse.track.subtitle
    }
}