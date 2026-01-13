package com.example.spotify

object PlaylistRepository {
    val playlists = mutableListOf<Playlist>()

    fun addPlaylist(playlist: Playlist) {
        playlists.add(playlist)
    }

}
