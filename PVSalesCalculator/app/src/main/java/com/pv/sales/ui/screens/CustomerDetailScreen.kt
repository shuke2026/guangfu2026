package com.pv.sales.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.pv.sales.data.entity.CalculationRecordEntity
import com.pv.sales.ui.theme.*
import com.pv.sales.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    navController: androidx.navigation.NavController,
    customerId: Long,
    viewModel: com.pv.sales.ui.viewmodel.CustomerDetailViewModel = viewModel(factory = ViewModelFactory(PVApplication.instance.database))
) {
    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }

    val customer by viewModel.customer.collectAsState()
    val records by viewModel.records.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("客户详情", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 客户信息卡片
            customer?.let { c ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("客户信息", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            InfoRow("姓名", c.name)
                            InfoRow("电话", c.phone)
                            if (c.address.isNotBlank()) InfoRow("地址", c.address)
                            if (c.province.isNotBlank()) InfoRow("省市", "${c.province} ${c.city}")
                        }
                    }
                }
            }

            // 历史测算记录
            item {
                Text("历史测算记录", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无测算记录", color = TextSecondary)
                    }
                }
            } else {
                items(records.size) { index ->
                    RecordCard(records[index]) {
                        navController.navigate(
                            com.pv.sales.ui.navigation.Screen.CalculationDetail.createRoute(records[index].id)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.width(60.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecordCard(record: CalculationRecordEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${record.province} ${record.city}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    formatDate(record.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text("装机: ${String.format("%.1f", record.installedCapacity)} kW | ${record.panelCount}块",
                style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("年发电: ${String.format("%.0f", record.yearlyGeneration)} kWh",
                style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("年收益: ¥${String.format("%.2f", record.yearlySaving)} | 回本: ${String.format("%.1f", record.staticPayback)}年",
                style = MaterialTheme.typography.bodySmall, color = AccentGreen, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.CHINA)
    return sdf.format(java.util.Date(timestamp))
}
