package com.example.spotify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(
    private val playlists: MutableList<Playlist>,
    private val onItemClick: (Playlist) -> Unit,
    private val onDeleteClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPlaylistName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvPlaylistDesc)
        val ivImage: ImageView = itemView.findViewById(R.id.ivPlaylistImage)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(playlist: Playlist) {
            tvName.text = playlist.name ?: "Playlist Tanpa Nama"
            tvDesc.text = playlist.description ?: "Tidak ada deskripsi"

            itemView.setOnClickListener {
                onItemClick(playlist)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(playlist)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}
