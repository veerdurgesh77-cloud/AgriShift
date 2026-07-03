package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agrishift_history")
data class AgriShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val rawInput: String,
    val workflow: String,
    val machineryType: String?,
    val landSizeAcres: Double?,
    val cropType: String?,
    val urgencyLevel: String?,
    val location: String?,
    val budget: Double?,
    val voiceCommandAction: String?,
    val originalLanguage: String?,
    val originalLandValue: String?,
    val season: String?,
    val estimatedCostInr: Double?,
    val estimatedTimeHours: Double?,
    val jsonResponse: String
)
