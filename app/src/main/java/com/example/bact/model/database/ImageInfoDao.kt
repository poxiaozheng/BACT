package com.example.bact.model.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageInfoDao {

    @Query("SELECT * from imageInfo ORDER BY imageName ASC")
    fun getItems(): Flow<List<ImageInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ImageInfo)

    @Delete
    suspend fun delete(item: ImageInfo)

}