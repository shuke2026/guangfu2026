package com.pv.sales.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun CalculationDetailScreen(
    navController: androidx.navigation.NavController,
    recordId: Long,
    viewModel: com.pv.sales.ui.viewmodel.CalculationDetailViewModel = viewModel(factory = ViewModelFactory(PVApplication.instance.database))
) {
    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    val record by viewModel.record.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("测算详情", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                    }
                },
                actions = {
                    record?.let { r ->
                        IconButton(onClick = {
                            viewModel.generatePdf(context, r)
                        }) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "生成PDF", tint = Color.White)
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, buildString {
                                    append("【光伏测算报告】\n")
                                    append("客户: ${r.customerName}\n")
                                    append("地区: ${r.province} ${r.city}\n")
                                    append("装机: ${String.format("%.1f", r.installedCapacity)} kW\n")
                                    append("年发电: ${String.format("%.0f", r.yearlyGeneration)} kWh\n")
                                    append("年收益: ¥${String.format("%.2f", r.yearlySaving)}\n")
                                    append("回本: ${String.format("%.1f", r.staticPayback)}年\n")
                                    append("碳减排: ${String.format("%.1f", r.carbonReduction)}吨")
                                })
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "分享报告"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "分享", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        record?.let { r ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 基本信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("项目概况", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                        InfoRow("客户", r.customerName.ifBlank { "未命名" })
                        InfoRow("地区", "${r.province} ${r.city}")
                        InfoRow("场景", r.scene)
                        InfoRow("日期", formatDate(r.createdAt))
                    }
                }

                // 装机信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("装机信息", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                        InfoRow("装机容量", "${String.format("%.1f", r.installedCapacity)} kW")
                        InfoRow("组件数量", "${r.panelCount} 块")
                        InfoRow("日均发电", "${String.format("%.1f", r.dailyGeneration)} kWh")
                        InfoRow("月均发电", "${String.format("%.0f", r.monthlyGeneration)} kWh")
                        InfoRow("年发电量", "${String.format("%.0f", r.yearlyGeneration)} kWh")
                        InfoRow("25年总发电", "${String.format("%.0f", r.total25yGeneration)} kWh")
                    }
                }

                // 投资收益
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("投资收益", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                        InfoRow("项目总投资", "¥${String.format("%.2f", r.totalInvestment)}")
                        InfoRow("月节电收益", "¥${String.format("%.2f", r.monthlySaving)}")
                        InfoRow("年节电收益", "¥${String.format("%.2f", r.yearlySaving)}")
                        InfoRow("25年总收益", "¥${String.format("%.2f", r.total25ySaving)}")
                        InfoRow("静态回本", "${String.format("%.1f", r.staticPayback)} 年")
                        InfoRow("动态回本", "${String.format("%.1f", r.dynamicPayback)} 年")
                    }
                }

                // 工厂指标
                if (r.coverageRatio > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("工厂用电分析", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                            InfoRow("用电覆盖", "${String.format("%.1f", r.coverageRatio)}%")
                            InfoRow("节电率", "${String.format("%.1f", r.savingRate)}%")
                        }
                    }
                }

                // 环保数据
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("环保贡献", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                        InfoRow("碳减排量", "${String.format("%.1f", r.carbonReduction)} 吨CO₂")
                        InfoRow("等效植树", "${String.format("%.0f", r.treesEquivalent)} 棵")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
