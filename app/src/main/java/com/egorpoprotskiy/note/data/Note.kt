package com.egorpoprotskiy.note.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//1.1 Создание сущности базы данных
@Entity(tableName = "note")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "heading")
    val heading: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "color")
    val color: Int,
)