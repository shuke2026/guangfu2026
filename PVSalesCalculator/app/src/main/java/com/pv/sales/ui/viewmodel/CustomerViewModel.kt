package com.pv.sales.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pv.sales.data.database.AppDatabase
import com.pv.sales.data.entity.CustomerEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(private val database: AppDatabase) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val customers: StateFlow<List<CustomerEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                database.customerDao().getAllCustomers()
            } else {
                database.customerDao().searchCustomers(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var showDialog = false
        private set

    private val _dialogName = MutableStateFlow("")
    val dialogName: StateFlow<String> = _dialogName.asStateFlow()

    private val _dialogPhone = MutableStateFlow("")
    val dialogPhone: StateFlow<String> = _dialogPhone.asStateFlow()

    private val _dialogAddress = MutableStateFlow("")
    val dialogAddress: StateFlow<String> = _dialogAddress.asStateFlow()

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun showAddDialog() {
        showDialog = true
        _dialogName.value = ""
        _dialogPhone.value = ""
        _dialogAddress.value = ""
    }

    fun hideDialog() {
        showDialog = false
    }

    fun updateDialogName(name: String) { _dialogName.value = name }
    fun updateDialogPhone(phone: String) { _dialogPhone.value = phone }
    fun updateDialogAddress(address: String) { _dialogAddress.value = address }

    fun saveCustomer() {
        viewModelScope.launch {
            val customer = CustomerEntity(
                name = _dialogName.value,
                phone = _dialogPhone.value,
                address = _dialogAddress.value
            )
            database.customerDao().insertCustomer(customer)
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            database.customerDao().deleteCustomer(customer)
        }
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CustomerViewModel(database) as T
        }
    }
}
