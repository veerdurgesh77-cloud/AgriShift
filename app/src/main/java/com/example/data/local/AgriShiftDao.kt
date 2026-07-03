package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AgriShiftDao {
    @Query("SELECT * FROM agrishift_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AgriShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AgriShiftEntity)

    @Delete
    suspend fun delete(entity: AgriShiftEntity)

    @Query("DELETE FROM agrishift_history")
    suspend fun clearAll()
}
