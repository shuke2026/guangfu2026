package com.pv.sales.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pv.sales.PVApplication
import com.pv.sales.data.entity.CustomerEntity
import com.pv.sales.ui.theme.*
import com.pv.sales.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    navController: androidx.navigation.NavController,
    viewModel: com.pv.sales.ui.viewmodel.CustomerViewModel = viewModel(factory = ViewModelFactory(PVApplication.instance.database))
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val customers by viewModel.customers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("客户档案", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
                actions = {
                    IconButton(onClick = { /* Show add dialog */ viewModel.showAddDialog() }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "添加客户", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                label = { Text("搜索客户姓名/电话") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                }
            )

            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextHint
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("暂无客户数据", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerCard(
                            customer = customer,
                            onClick = { navController.navigate(com.pv.sales.ui.navigation.Screen.CustomerDetail.createRoute(customer.id)) },
                            onDelete = { viewModel.deleteCustomer(customer) }
                        )
                    }
                }
            }
        }
    }

    // 添加客户对话框
    if (viewModel.showDialog) {
        AddCustomerDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hideDialog() }
        )
    }
}

@Composable
fun CustomerCard(
    customer: CustomerEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = PrimaryBlueLight.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        customer.name.take(1),
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(customer.phone, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (customer.address.isNotBlank()) {
                    Text(
                        customer.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = AccentRed)
            }
        }
    }
}

@Composable
fun AddCustomerDialog(
    viewModel: com.pv.sales.ui.viewmodel.CustomerViewModel,
    onDismiss: () -> Unit
) {
    val name by viewModel.dialogName.collectAsState()
    val phone by viewModel.dialogPhone.collectAsState()
    val address by viewModel.dialogAddress.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加客户") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateDialogName(it) },
                    label = { Text("客户姓名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { viewModel.updateDialogPhone(it) },
                    label = { Text("联系电话") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { viewModel.updateDialogAddress(it) },
                    label = { Text("详细地址") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.saveCustomer()
                onDismiss()
            }) {
                Text("保存", color = PrimaryBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
