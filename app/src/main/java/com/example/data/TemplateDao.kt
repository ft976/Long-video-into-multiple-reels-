package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM cut_templates ORDER BY timestamp DESC")
    fun getAllTemplates(): Flow<List<CutTemplate>>

    @Query("SELECT * FROM cut_templates WHERE id = :id Limit 1")
    suspend fun getTemplateById(id: Int): CutTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: CutTemplate)

    @Query("DELETE FROM cut_templates WHERE id = :id")
    suspend fun deleteTemplateById(id: Int)
}
