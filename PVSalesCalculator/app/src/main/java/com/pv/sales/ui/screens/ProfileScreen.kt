package com.pv.sales.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pv.sales.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logo区域
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(36.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SolarPower,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = PrimaryBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "光伏销售智能测算系统",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "PV Sales Calculator v1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // 设置项
            SettingGroup("公司信息") {
                SettingItem(Icons.Default.Business, "公司名称", "请设置") { }
                SettingItem(Icons.Default.Phone, "联系电话", "请设置") { }
                SettingItem(Icons.Default.LocationOn, "公司地址", "请设置") { }
            }

            SettingGroup("系统设置") {
                SettingItem(Icons.Default.Image, "公司LOGO", "点击设置") { }
                SettingItem(Icons.Default.Palette, "报告主题", "商务蓝") { }
                SettingItem(Icons.Default.Storage, "数据管理", "清除缓存") { }
            }

            SettingGroup("关于") {
                SettingItem(Icons.Default.Info, "版本信息", "v1.0.0") { }
                SettingItem(Icons.Default.Description, "使用说明", "查看") { }
                SettingItem(Icons.Default.ContactSupport, "技术支持", "联系客服") { }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = TextSecondary) },
        leadingContent = {
            Icon(icon, contentDescription = null, tint = PrimaryBlue)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}
