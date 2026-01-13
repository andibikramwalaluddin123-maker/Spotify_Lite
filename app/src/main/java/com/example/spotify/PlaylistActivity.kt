package com.example.spotify

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PlaylistActivity : AppCompatActivity() {

    private lateinit var tvPlaylistName: TextView
    private lateinit var btnEditPlaylist: Button
    private lateinit var btnAddSong: Button
    private lateinit var rvSongs: RecyclerView
    private lateinit var songAdapter: SongAdapter

    private val songList = mutableListOf<Song>()
    private lateinit var playlistId: String

    private val playlistRef =
        FirebaseDatabase.getInstance().getReference("playlists")

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        // ===============================
        // AMBIL PLAYLIST ID
        // ===============================
        playlistId = intent.getStringExtra("playlistId") ?: ""
        if (playlistId.isEmpty()) {
            Toast.makeText(this, "Playlist tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ===============================
        // INIT VIEW
        // ===============================
        tvPlaylistName = findViewById(R.id.tvPlaylistName)
        btnEditPlaylist = findViewById(R.id.btnEditPlaylist)
        btnAddSong = findViewById(R.id.btnAddSong)
        rvSongs = findViewById(R.id.rvSongs)

        rvSongs.layoutManager = LinearLayoutManager(this)

        songAdapter = SongAdapter(songList) { song ->
            playSong(song)
        }
        rvSongs.adapter = songAdapter

        loadPlaylist()

        btnEditPlaylist.setOnClickListener {
            showEditPlaylistDialog()
        }

        btnAddSong.setOnClickListener {
            showAddSongDialog()
        }
    }

    // ===============================
    // LOAD PLAYLIST
    // ===============================
    private fun loadPlaylist() {
        playlistRef.child(playlistId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlist = snapshot.getValue(Playlist::class.java)
                    if (playlist == null) {
                        Toast.makeText(
                            this@PlaylistActivity,
                            "Playlist tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    tvPlaylistName.text = playlist.name

                    // Lagu offline awal
                    songList.clear()
                    songList.addAll(
                        listOf(
                            Song("1", "Negara Lucu", "Enau", R.raw.enau_negara_lucu),
                            Song("2", "Cup of Joe", "Multo", R.raw.multo_cup_of_loe),
                            Song("3", "Nina", "Feast", R.raw.feast_nina)
                        )
                    )
                    songAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PlaylistActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ===============================
    // PLAY SONG
    // ===============================
    private fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, song.resId)
        mediaPlayer?.start()

        Toast.makeText(this, "Memutar: ${song.title}", Toast.LENGTH_SHORT).show()
    }

    // ===============================
    // TAMBAH LAGU (DIALOG)
    // ===============================
    private fun showAddSongDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_song, null)

        val etTitle = view.findViewById<EditText>(R.id.etSongTitle)
        val etArtist = view.findViewById<EditText>(R.id.etSongArtist)

        AlertDialog.Builder(this)
            .setTitle("Tambah Lagu")
            .setView(view)
            .setPositiveButton("Tambah") { _, _ ->
                val title = etTitle.text.toString().trim()
                val artist = etArtist.text.toString().trim()

                if (title.isEmpty() || artist.isEmpty()) {
                    Toast.makeText(this, "Judul & artis wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Lagu dummy (belum ada audio)
                val newSong = Song(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    artist = artist,
                    resId = R.raw.enau_negara_lucu // dummy audio
                )

                songList.add(newSong)
                songAdapter.notifyItemInserted(songList.size - 1)

                Toast.makeText(this, "Lagu ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ===============================
    // EDIT PLAYLIST
    // ===============================
    private fun showEditPlaylistDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_edit_playlist, null)

        val etName = view.findViewById<EditText>(R.id.etPlaylistName)
        val etDesc = view.findViewById<EditText>(R.id.etPlaylistDesc)

        etName.setText(tvPlaylistName.text.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Playlist")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = etName.text.toString().trim()
                val newDesc = etDesc.text.toString().trim()

                if (newName.isEmpty()) {
                    Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val update = mapOf(
                    "name" to newName,
                    "description" to newDesc
                )

                playlistRef.child(playlistId).updateChildren(update)
                tvPlaylistName.text = newName
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
