package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var rvPlaylists: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter
    private val playlistList = mutableListOf<Playlist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ================== RECYCLER VIEW ==================
        rvPlaylists = findViewById(R.id.rvPlaylists)
        rvPlaylists.layoutManager = LinearLayoutManager(this)

        playlistAdapter = PlaylistAdapter(
            playlistList,
            onItemClick = { playlist ->
                val intent = Intent(this, PlaylistActivity::class.java)
                intent.putExtra("playlistId", playlist.id)
                startActivity(intent)
            },
            onDeleteClick = { playlist ->
                showDeleteDialog(playlist)
            }
        )

        rvPlaylists.adapter = playlistAdapter

        // ================== OBSERVE DATA ==================
        observePlaylists()

        // ================== ADD PLAYLIST ==================
        val btnAddPlaylist = findViewById<Button>(R.id.btnAddPlaylist)
        btnAddPlaylist.setOnClickListener {
            startActivity(Intent(this, CreatePlaylistActivity::class.java))
        }

        // ================== BOTTOM NAV ==================
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    // ================== FIREBASE OBSERVER ==================
    private fun observePlaylists() {
        val dbRef = FirebaseDatabase.getInstance().getReference("playlists")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playlistList.clear()
                for (child in snapshot.children) {
                    val playlist = child.getValue(Playlist::class.java)
                    playlist?.let {
                        it.id = child.key
                        playlistList.add(it)
                    }
                }
                playlistAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ================== DELETE PLAYLIST ==================
    private fun showDeleteDialog(playlist: Playlist) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Playlist")
            .setMessage("Apakah kamu yakin ingin menghapus playlist ini?")
            .setPositiveButton("Hapus") { _, _ ->
                playlist.id?.let { id ->
                    FirebaseDatabase.getInstance()
                        .getReference("playlists")
                        .child(id)
                        .removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Playlist dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
