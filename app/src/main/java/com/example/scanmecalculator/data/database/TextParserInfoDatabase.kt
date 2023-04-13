package com.example.scanmecalculator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scanmecalculator.data.database.entity.TextParserInfoEntity

@Database(
    entities = [TextParserInfoEntity::class],
    version = 1
)
abstract class TextParserInfoDatabase: RoomDatabase() {
    abstract val dao: TextParserInfoDao
}
