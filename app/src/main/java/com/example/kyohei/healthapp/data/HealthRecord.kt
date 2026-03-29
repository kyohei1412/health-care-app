package com.example.kyohei.healthapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Double,
    val systolic: Int,
    val diastolic: Int,
    val bodyFatPct: Double?,
    val recordedAt: Long
)
