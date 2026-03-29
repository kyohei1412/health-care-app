package com.example.kyohei.healthapp.data

import kotlinx.coroutines.flow.Flow

class HealthRepository(
    private val healthDao: HealthDao,
    private val heightPreferences: HeightPreferences
) {
    
    val allRecords: Flow<List<HealthRecord>> = healthDao.getAllRecords()
    val allRecordsAsc: Flow<List<HealthRecord>> = healthDao.getAllRecordsAsc()
    val heightSetting: Flow<Double?> = heightPreferences.heightCmFlow

    suspend fun insertRecord(record: HealthRecord) {
        healthDao.insertRecord(record)
    }

    suspend fun deleteRecord(record: HealthRecord) {
        healthDao.deleteRecord(record)
    }

    suspend fun deleteById(id: Long) {
        healthDao.deleteById(id)
    }

    suspend fun saveHeight(heightCm: Double) {
        heightPreferences.setHeightCm(heightCm)
    }
}
