package com.pv.sales.data.repository

import com.pv.sales.data.dao.CalculationRecordDao
import com.pv.sales.data.entity.CalculationRecordEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val recordDao: CalculationRecordDao) {

    fun getAllRecords(): Flow<List<CalculationRecordEntity>> = recordDao.getAllRecords()

    fun searchRecords(query: String): Flow<List<CalculationRecordEntity>> =
        if (query.isBlank()) recordDao.getAllRecords()
        else recordDao.searchRecords(query)

    fun getRecordsByCustomerId(customerId: Long): Flow<List<CalculationRecordEntity>> =
        recordDao.getRecordsByCustomerId(customerId)

    suspend fun getRecordById(id: Long): CalculationRecordEntity? = recordDao.getRecordById(id)

    suspend fun insertRecord(record: CalculationRecordEntity): Long = recordDao.insertRecord(record)

    suspend fun deleteRecord(record: CalculationRecordEntity) = recordDao.deleteRecord(record)

    suspend fun deleteRecordById(id: Long) = recordDao.deleteRecordById(id)
}
