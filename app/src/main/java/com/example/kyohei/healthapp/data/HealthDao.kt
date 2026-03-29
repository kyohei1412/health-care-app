package com.example.kyohei.healthapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Insert
    suspend fun insertRecord(record: HealthRecord)

    @Query("SELECT * FROM health_records ORDER BY recordedAt DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records ORDER BY recordedAt ASC")
    fun getAllRecordsAsc(): Flow<List<HealthRecord>>

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("DELETE FROM health_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
