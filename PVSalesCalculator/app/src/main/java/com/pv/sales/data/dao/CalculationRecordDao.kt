package com.pv.sales.data.dao

import androidx.room.*
import com.pv.sales.data.entity.CalculationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationRecordDao {
    @Query("SELECT * FROM calculation_records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<CalculationRecordEntity>>

    @Query("SELECT * FROM calculation_records WHERE id = :id")
    suspend fun getRecordById(id: Long): CalculationRecordEntity?

    @Query("SELECT * FROM calculation_records WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getRecordsByCustomerId(customerId: Long): Flow<List<CalculationRecordEntity>>

    @Query("SELECT * FROM calculation_records WHERE customerName LIKE '%' || :query || '%' OR province LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%'")
    fun searchRecords(query: String): Flow<List<CalculationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CalculationRecordEntity): Long

    @Delete
    suspend fun deleteRecord(record: CalculationRecordEntity)

    @Query("DELETE FROM calculation_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
}
