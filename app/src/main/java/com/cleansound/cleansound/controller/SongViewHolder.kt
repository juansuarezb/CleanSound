package com.cleansound.cleansound.controller

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val ivAlbumArt: ImageView = itemView.findViewById(R.id.ivAlbumArt)
    val tvSongTitle: TextView = itemView.findViewById(R.id.tvSongTitle)
    val tvSongArtist: TextView = itemView.findViewById(R.id.tvSongArtist)
    val tvSongDuration: TextView = itemView.findViewById(R.id.tvSongDuration)
}