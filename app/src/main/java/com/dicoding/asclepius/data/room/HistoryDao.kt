package com.dicoding.asclepius.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAll(): LiveData<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    fun getById(id: Int): HistoryEntity

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: HistoryEntity)
}