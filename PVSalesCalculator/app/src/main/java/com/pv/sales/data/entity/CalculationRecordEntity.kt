package com.pv.sales.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_records")
data class CalculationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long = 0,
    val customerName: String = "",
    val roofArea: Double,
    val scene: String,
    val province: String,
    val city: String,
    val installedCapacity: Double,
    val panelCount: Int,
    val dailyGeneration: Double,
    val monthlyGeneration: Double,
    val yearlyGeneration: Double,
    val total25yGeneration: Double,
    val totalInvestment: Double,
    val monthlySaving: Double,
    val yearlySaving: Double,
    val total25ySaving: Double,
    val staticPayback: Double,
    val dynamicPayback: Double,
    val coverageRatio: Double,
    val savingRate: Double,
    val carbonReduction: Double,
    val treesEquivalent: Double,
    val generationMode: String = "SELF_USE",
    val industryType: String = "",
    val electricityType: String = "RESIDENTIAL",
    val panelPower: Int = 550,
    val createdAt: Long = System.currentTimeMillis()
)
