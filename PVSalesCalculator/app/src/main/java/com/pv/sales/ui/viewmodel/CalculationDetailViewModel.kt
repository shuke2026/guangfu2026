package com.pv.sales.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pv.sales.data.database.AppDatabase
import com.pv.sales.data.entity.CalculationRecordEntity
import com.pv.sales.model.CalculationResult
import com.pv.sales.util.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculationDetailViewModel(private val database: AppDatabase) : ViewModel() {

    private val _record = MutableStateFlow<CalculationRecordEntity?>(null)
    val record: StateFlow<CalculationRecordEntity?> = _record.asStateFlow()

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            _record.value = database.calculationRecordDao().getRecordById(recordId)
        }
    }

    fun generatePdf(context: Context, record: CalculationRecordEntity) {
        viewModelScope.launch {
            try {
                val result = recordToResult(record)
                val file = PdfGenerator.generate(context, result)
                Toast.makeText(context, "PDF已生成: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                PdfGenerator.sharePdf(context, file)
            } catch (e: Exception) {
                Toast.makeText(context, "PDF生成失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun recordToResult(record: CalculationRecordEntity): CalculationResult {
        return CalculationResult(
            roofArea = record.roofArea,
            scene = record.scene,
            province = record.province,
            city = record.city,
            installedCapacity = record.installedCapacity,
            panelCount = record.panelCount,
            dailyGeneration = record.dailyGeneration,
            monthlyGeneration = record.monthlyGeneration,
            yearlyGeneration = record.yearlyGeneration,
            total25yGeneration = record.total25yGeneration,
            totalInvestment = record.totalInvestment,
            monthlySaving = record.monthlySaving,
            yearlySaving = record.yearlySaving,
            total25ySaving = record.total25ySaving,
            staticPayback = record.staticPayback,
            dynamicPayback = record.dynamicPayback,
            coverageRatio = record.coverageRatio,
            savingRate = record.savingRate,
            carbonReduction = record.carbonReduction,
            treesEquivalent = record.treesEquivalent,
            generationMode = record.generationMode,
            electricityType = record.electricityType,
            industryType = record.industryType,
            panelPower = record.panelPower
        )
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalculationDetailViewModel(database) as T
        }
    }
}
