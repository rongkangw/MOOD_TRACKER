package com.example.moodtracker

import androidx.room.Entity

@Entity(tableName = "entryData", primaryKeys = ["month", "day"])
data class EntryDay(
    val id : String,
    val month : Int,
    val day : Int,
    val mood : Int
)