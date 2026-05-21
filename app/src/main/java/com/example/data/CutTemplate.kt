package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cut_templates")
data class CutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val aspectRatio: String, // e.g., "1:1", "9:16", "16:9"
    val durationSecs: Int, // e.g., 15, 30, 60
    val targetPlatform: String, // e.g., "Instagram Reels", "YouTube Shorts", "TikTok", "Universal"
    val smartCutEnabled: Boolean = true,
    val skipSilentParts: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
