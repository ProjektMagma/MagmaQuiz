package com.github.projektmagma.magmaquiz.app.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerConfigDao {
    @Insert
    suspend fun insert(item: ServerConfigEntity)
    
    @Update
    suspend fun update(item: ServerConfigEntity)
    
    @Query("SELECT * FROM SERVERCONFIGENTITY ORDER BY modifiedAt DESC LIMIT 5")
    fun getAllConfigsDesc(): Flow<List<ServerConfigEntity>>
    
    @Query("SELECT * FROM SERVERCONFIGENTITY WHERE isSelected IS TRUE")
    suspend fun getSelectedConfig(): ServerConfigEntity
    
    @Query("UPDATE SERVERCONFIGENTITY SET isSelected = 0")
    suspend fun deselectAll()
    
    @Delete
    suspend fun delete(item: ServerConfigEntity)
}