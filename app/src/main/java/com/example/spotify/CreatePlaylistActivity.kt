package com.example.spotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase

class CreatePlaylistActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "playlist_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_playlist)

        createNotificationChannel()

        val etName = findViewById<EditText>(R.id.etPlaylistName)
        val etDesc = findViewById<EditText>(R.id.etPlaylistDesc)
        val btnSave = findViewById<Button>(R.id.btnSavePlaylist)

        btnSave.setOnClickListener {

            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Nama wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Firebase reference
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("playlists")
                .push()

            val playlist = Playlist(
                id = dbRef.key,
                name = name,
                description = desc
            )

            dbRef.setValue(playlist)
                .addOnSuccessListener {

                    showHeadsUpNotification(
                        title = "Playlist dibuat",
                        message = "Playlist \"$name\" berhasil ditambahkan"
                    )

                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    // ================= NOTIFICATION =================

    private fun showHeadsUpNotification(title: String, message: String) {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // HEADS UP
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playlist Notification",
                NotificationManager.IMPORTANCE_HIGH // WAJIB HIGH
            ).apply {
                description = "Notifikasi playlist Spotify Lite"
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
