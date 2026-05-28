package com.pv.sales.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pv.sales.data.database.AppDatabase
import com.pv.sales.data.entity.CalculationRecordEntity
import com.pv.sales.data.entity.CustomerEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerDetailViewModel(private val database: AppDatabase) : ViewModel() {

    private val _customer = MutableStateFlow<CustomerEntity?>(null)
    val customer: StateFlow<CustomerEntity?> = _customer.asStateFlow()

    private val _records = MutableStateFlow<List<CalculationRecordEntity>>(emptyList())
    val records: StateFlow<List<CalculationRecordEntity>> = _records.asStateFlow()

    fun loadCustomer(customerId: Long) {
        viewModelScope.launch {
            _customer.value = database.customerDao().getCustomerById(customerId)
            database.calculationRecordDao().getRecordsByCustomerId(customerId).collect { records ->
                _records.value = records
            }
        }
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CustomerDetailViewModel(database) as T
        }
    }
}
