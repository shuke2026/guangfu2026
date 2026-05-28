package com.pv.sales.data.repository

import com.pv.sales.data.dao.CustomerDao
import com.pv.sales.data.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val customerDao: CustomerDao) {

    fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> =
        if (query.isBlank()) customerDao.getAllCustomers()
        else customerDao.searchCustomers(query)

    suspend fun getCustomerById(id: Long): CustomerEntity? = customerDao.getCustomerById(id)

    suspend fun insertCustomer(customer: CustomerEntity): Long = customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)

    suspend fun deleteCustomerById(id: Long) = customerDao.deleteCustomerById(id)
}
