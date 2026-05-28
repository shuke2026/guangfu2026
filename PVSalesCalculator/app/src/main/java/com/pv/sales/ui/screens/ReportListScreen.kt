package com.pv.sales.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pv.sales.PVApplication
import com.pv.sales.ui.theme.*
import com.pv.sales.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(
    navController: androidx.navigation.NavController,
    viewModel: com.pv.sales.ui.viewmodel.ReportViewModel = viewModel(factory = ViewModelFactory(PVApplication.instance.database))
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val records by viewModel.records.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("报告记录", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                label = { Text("搜索报告") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextHint)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("暂无报告记录", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        ReportCard(
                            record = record,
                            onClick = {
                                navController.navigate(
                                    com.pv.sales.ui.navigation.Screen.CalculationDetail.createRoute(record.id)
                                )
                            },
                            onShare = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "光伏测算报告 - ${record.customerName}\n" +
                                            "装机: ${String.format("%.1f", record.installedCapacity)}kW\n" +
                                            "年收益: ¥${String.format("%.2f", record.yearlySaving)}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "分享报告"))
                            },
                            onDelete = { viewModel.deleteRecord(record) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    record: com.pv.sales.data.entity.CalculationRecordEntity,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (record.customerName.isNotBlank()) record.customerName else "未命名客户",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "${record.province} ${record.city} | ${record.scene}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(formatDate(record.createdAt), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("装机容量", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${String.format("%.1f", record.installedCapacity)} kW", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("年收益", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("¥${String.format("%.2f", record.yearlySaving)}", fontWeight = FontWeight.Bold, color = AccentGreen)
                }
                Column {
                    Text("回本周期", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${String.format("%.1f", record.staticPayback)} 年", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("查看")
                }
                TextButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("分享")
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentRed)
                }
            }
        }
    }
}
