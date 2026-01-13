package com.example.spotify

data class Song(
    var id: String = "",
    var title: String = "",
    var artist: String = "",
    val resId: Int,
    val audioResId: Int = 0,
    val audioUrl: String? = null
)




