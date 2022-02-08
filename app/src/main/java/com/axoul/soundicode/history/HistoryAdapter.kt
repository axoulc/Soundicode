package com.axoul.soundicode.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axoul.soundicode.R
import com.axoul.soundicode.communication.JsonResponse
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val mResponse: List<JsonResponse?>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    companion object {
        private val df = SimpleDateFormat("dd/MM/yyyy\nHH:mm:ss")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title_txt)
        var artist: TextView = itemView.findViewById(R.id.artist_txt)
        var date: TextView = itemView.findViewById(R.id.date_txt)
        var imgHist: ImageView = itemView.findViewById(R.id.img_hist)

    }

    interface OnAdapterItemClickListener {
        fun onAdapterViewClick(view: View)
    }

    private var mContext: Context? = null
    private var onAdapterItemClickListener: OnAdapterItemClickListener? = null
    private val onClickListener = View.OnClickListener { v ->
        if (onAdapterItemClickListener != null) {
            onAdapterItemClickListener!!.onAdapterViewClick(v)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        val historyView = inflater.inflate(R.layout.item_history, parent, false)
        historyView.setOnClickListener(onClickListener)
        return ViewHolder(historyView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resp = mResponse[position]
        val mTitle = holder.title
        mTitle.text = resp?.track?.title
        val mArtist = holder.artist
        mArtist.text = resp?.track?.subtitle
        val mDate = holder.date
        val currentDate = Date(resp!!.timestamp)
        mDate.text = df.format(currentDate)
        val mImg = holder.imgHist
        Glide.with(mContext!!).load(resp.track.images.coverart).into(mImg)
    }

    override fun getItemCount(): Int {
        return mResponse.size
    }

    fun setOnAdapterItemClickListener(onAdapterItemClickListener: OnAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener
    }

    fun getItemAtPosition(childAdapterPosition: Int): JsonResponse? {
        return mResponse[childAdapterPosition]
    }
}