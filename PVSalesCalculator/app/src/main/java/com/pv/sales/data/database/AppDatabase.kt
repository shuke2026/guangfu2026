package com.pv.sales.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pv.sales.data.dao.CalculationRecordDao
import com.pv.sales.data.dao.CustomerDao
import com.pv.sales.data.entity.CalculationRecordEntity
import com.pv.sales.data.entity.CustomerEntity

@Database(
    entities = [CustomerEntity::class, CalculationRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun calculationRecordDao(): CalculationRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pv_sales_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
