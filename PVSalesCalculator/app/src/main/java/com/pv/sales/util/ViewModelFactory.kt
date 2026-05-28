package com.pv.sales.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pv.sales.data.database.AppDatabase

/**
 * 通用 ViewModelFactory
 * 用于向 ViewModel 传递数据库实例
 */
class ViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val factory = when (modelClass) {
            com.pv.sales.ui.viewmodel.HomeViewModel::class.java ->
                com.pv.sales.ui.viewmodel.HomeViewModel.Factory(database)
            com.pv.sales.ui.viewmodel.CustomerViewModel::class.java ->
                com.pv.sales.ui.viewmodel.CustomerViewModel.Factory(database)
            com.pv.sales.ui.viewmodel.CustomerDetailViewModel::class.java ->
                com.pv.sales.ui.viewmodel.CustomerDetailViewModel.Factory(database)
            com.pv.sales.ui.viewmodel.ReportViewModel::class.java ->
                com.pv.sales.ui.viewmodel.ReportViewModel.Factory(database)
            com.pv.sales.ui.viewmodel.CalculationDetailViewModel::class.java ->
                com.pv.sales.ui.viewmodel.CalculationDetailViewModel.Factory(database)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return factory.create(modelClass)
    }
}
