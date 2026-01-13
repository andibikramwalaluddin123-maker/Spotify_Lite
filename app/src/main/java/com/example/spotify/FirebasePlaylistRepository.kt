package com.example.spotify

import com.google.firebase.database.FirebaseDatabase

class FirebasePlaylistRepository {

    private val db = FirebaseDatabase.getInstance()
        .getReference("playlists")

    fun createPlaylist(
        playlist: Playlist,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val id = db.push().key
        if (id == null) {
            onError("Gagal membuat ID playlist")
            return
        }

        playlist.id = id

        db.child(id)
            .setValue(playlist)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }

    fun updatePlaylist(
        playlist: Playlist,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val id = playlist.id ?: return
        db.child(id)
            .setValue(playlist)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error") }
    }
}
