package com.dicoding.asclepius.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    var id: Int,

    @field:ColumnInfo(name = "image")
    var image: String,

    @field:ColumnInfo(name = "label")
    var label: String,

    @field:ColumnInfo(name = "confidence")
    var confidence: Float,

    @field:ColumnInfo(name = "date")
    var date: String
)