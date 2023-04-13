package com.example.scanmecalculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scanmecalculator.data.database.entity.TextParserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextParserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextParserInfo(textParserInfoEntity: TextParserInfoEntity)

    @Query(
        """
            SELECT *
            FROM textparserinfoentity
        """
    )
    fun getTextParserInfoList(): Flow<List<TextParserInfoEntity>>
}
