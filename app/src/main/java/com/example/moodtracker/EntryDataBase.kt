package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EntryDay::class], version = 1)
abstract class EntryDataBase: RoomDatabase() {
    abstract val dao: EntryDayDao

    companion object {
        @Volatile
        private var Instance: EntryDataBase? = null

        fun getDatabase(context: Context): EntryDataBase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EntryDataBase::class.java, "entryData")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}