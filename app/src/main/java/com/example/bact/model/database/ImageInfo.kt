package com.example.bact.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "scale")
    val scale: Int,
    @ColumnInfo(name = "noiseGrade")
    val noiseGrade: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "imageName")
    val imageName: String
)