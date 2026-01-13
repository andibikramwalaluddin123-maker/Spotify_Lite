package com.example.spotify

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class EditPlaylistActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var btnSave: Button
    private lateinit var playlistId: String

    private val dbRef = FirebaseDatabase.getInstance().getReference("playlists")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_playlist)

        // ===== 1. Ambil ID playlist dari intent =====
        playlistId = intent.getStringExtra("playlistId") ?: ""
        if (playlistId.isEmpty()) {
            Toast.makeText(this, "Playlist tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ===== 2. Init views =====
        etName = findViewById(R.id.etPlaylistName)
        etDesc = findViewById(R.id.etPlaylistDesc)
        btnSave = findViewById(R.id.btnSave)

        // ===== 3. Load data playlist dari Firebase =====
        loadPlaylist()

        // ===== 4. Simpan update =====
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Nama playlist tidak boleh kosong"
                return@setOnClickListener
            }

            updatePlaylist(name, desc)
        }
    }

    private fun loadPlaylist() {
        dbRef.child(playlistId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlist = snapshot.getValue(Playlist::class.java)
                if (playlist != null) {
                    etName.setText(playlist.name)
                    etDesc.setText(playlist.description)
                } else {
                    Toast.makeText(this@EditPlaylistActivity, "Playlist tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditPlaylistActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePlaylist(name: String, desc: String) {
        val updates = mapOf<String, Any>(
            "name" to name,
            "description" to desc
        )

        dbRef.child(playlistId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Playlist berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish() // kembali ke PlaylistActivity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
