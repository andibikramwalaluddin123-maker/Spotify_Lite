package com.example.spotify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectSongAdapter(
    private val songs: List<Song>,
    private val selectedSongs: MutableSet<Song>
) : RecyclerView.Adapter<SelectSongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvSongTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvSongArtist)
        val checkBox: CheckBox = itemView.findViewById(R.id.cbSelect)

        fun bind(song: Song) {
            tvTitle.text = song.title
            tvArtist.text = song.artist
            checkBox.isChecked = selectedSongs.contains(song)

            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedSongs.add(song) else selectedSongs.remove(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
