/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bact.model.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bact.BACTApplication

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [ImageInfo::class], version = 1, exportSchema = false)
abstract class ImageInfoRoomDatabase : RoomDatabase() {

    abstract fun imageInfoDao(): ImageInfoDao

    companion object {
        @Volatile
        private var INSTANCE: ImageInfoRoomDatabase? = null

        fun getDatabase(): ImageInfoRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    BACTApplication.appContext,
                    ImageInfoRoomDatabase::class.java,
                    "imageInfo_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}