package com.cleansound.cleansound.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import model.Playlist

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {  // ← Fíjate aquí

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)
        val tvName: TextView = itemView.findViewById(R.id.tvPlaylistName)
        val tvSongCount: TextView = itemView.findViewById(R.id.tvSongCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.ivCover.setImageResource(playlist.coverImage)
        holder.tvName.text = playlist.name
        holder.tvSongCount.text = "${playlist.songCount} canciones"

        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}