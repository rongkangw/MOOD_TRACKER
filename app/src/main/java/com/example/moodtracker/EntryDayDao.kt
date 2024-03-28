package com.example.moodtracker

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDayDao {

    @Query("SELECT * FROM entryData WHERE entryData.month = :month")
    fun getMonthRecords(month: Int): Flow<List<EntryDay>>

    @Query("SELECT * FROM entryData WHERE entryData.month = :month and entryData.day = :day")
    fun getEntry(month: Int, day: Int): Flow<EntryDay>

    @Upsert
    suspend fun upsertEntry(entry : EntryDay)

    @Update
    suspend fun updateEntry(entry : EntryDay)

    @Query("DELETE FROM entryData")
    suspend fun deleteAllRecords()

    @Query("SELECT COUNT(*) FROM entryData WHERE entryData.mood = :mood")
    fun getNumberOfMood(mood : String): Flow<Int>

    @Query("SELECT mood FROM entryData")
    fun getAverageMood(): Flow<List<String>>
}