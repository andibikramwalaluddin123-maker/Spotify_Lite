package com.example.spotify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog


class SongAdapter(
    private val songs: MutableList<Song>,
    private val onPlay: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvSongTitle)
        val tvArtist: TextView = view.findViewById(R.id.tvSongArtist)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist

        // ▶️ Play lagu
        holder.itemView.setOnClickListener {
            onPlay(song)
        }

        // 🗑️ Konfirmasi hapus lagu
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hapus Lagu")
                .setMessage("Yakin ingin menghapus lagu \"${song.title}\" dari playlist?")
                .setPositiveButton("Hapus") { _, _ ->
                    val pos = holder.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        songs.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun getItemCount(): Int = songs.size
}



