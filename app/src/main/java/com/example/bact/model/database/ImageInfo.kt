package com.example.bact.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageByteArray: ByteArray,
    val scale: Int,
    val noiseGrade: Int,
    val date: String,
    val imageName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageInfo

        if (!imageByteArray.contentEquals(other.imageByteArray)) return false
        if (scale != other.scale) return false
        if (noiseGrade != other.noiseGrade) return false
        if (date != other.date) return false
        if (imageName != other.imageName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageByteArray.contentHashCode()
        result = 31 * result + scale
        result = 31 * result + noiseGrade
        result = 31 * result + date.hashCode()
        result = 31 * result + imageName.hashCode()
        return result
    }
}