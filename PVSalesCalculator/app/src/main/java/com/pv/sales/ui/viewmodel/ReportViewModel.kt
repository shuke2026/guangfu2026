package com.pv.sales.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pv.sales.data.database.AppDatabase
import com.pv.sales.data.entity.CalculationRecordEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReportViewModel(private val database: AppDatabase) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val records: StateFlow<List<CalculationRecordEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                database.calculationRecordDao().getAllRecords()
            } else {
                database.calculationRecordDao().searchRecords(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun deleteRecord(record: CalculationRecordEntity) {
        viewModelScope.launch {
            database.calculationRecordDao().deleteRecord(record)
        }
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportViewModel(database) as T
        }
    }
}
