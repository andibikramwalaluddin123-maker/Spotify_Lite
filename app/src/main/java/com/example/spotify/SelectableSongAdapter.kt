package com.example.spotify

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class SelectableSongAdapter(private val songs: List<Song>) :
    RecyclerView.Adapter<SelectableSongAdapter.ViewHolder>() {

    val selectedSongs = mutableListOf<Song>()

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)) {

        val checkBox: CheckBox = itemView.findViewById(android.R.id.text1) as CheckBox

        fun bind(song: Song) {
            checkBox.text = "${song.title} - ${song.artist}"
            checkBox.isChecked = selectedSongs.contains(song)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedSongs.add(song)
                else selectedSongs.remove(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
