package com.pv.sales.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pv.sales.PVApplication
import com.pv.sales.data.SolarDatabase
import com.pv.sales.data.enums.*
import com.pv.sales.model.CalculationResult
import com.pv.sales.ui.theme.*
import com.pv.sales.ui.viewmodel.HomeViewModel
import com.pv.sales.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(PVApplication.instance.database))
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current

    // 省市数据
    val provinces = remember { SolarDatabase.getProvinces() }
    val cities = remember(uiState.selectedProvince) {
        if (uiState.selectedProvince.isNotBlank()) {
            SolarDatabase.getCities(uiState.selectedProvince)
        } else emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "光伏智能测算",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 模式切换
            ModeSwitcher(
                isProMode = uiState.isProMode,
                onModeChange = { viewModel.updateProMode(it) }
            )

            // 基础输入区
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("基础参数", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // 屋顶面积
                    OutlinedTextField(
                        value = uiState.roofArea,
                        onValueChange = { viewModel.updateRoofArea(it) },
                        label = { Text("屋顶可用面积（㎡）") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null) }
                    )

                    // 安装场景
                    Text("安装场景", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InstallScene.values().forEach { scene ->
                            val selected = uiState.selectedScene == scene
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.updateScene(scene) },
                                label = { Text(scene.label) },
                                leadingIcon = if (selected) ({
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }) else null
                            )
                        }
                    }

                    // 省市选择
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 省份
                        ExposedDropdownMenuBox(
                            expanded = uiState.provinceExpanded,
                            onExpandedChange = { viewModel.updateProvinceExpanded(it) }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedProvince,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("省份") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.provinceExpanded) },
                                modifier = Modifier.weight(1f)
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.provinceExpanded,
                                onDismissRequest = { viewModel.updateProvinceExpanded(false) }
                            ) {
                                provinces.forEach { province ->
                                    DropdownMenuItem(
                                        text = { Text(province) },
                                        onClick = {
                                            viewModel.updateProvince(province)
                                            viewModel.updateProvinceExpanded(false)
                                        }
                                    )
                                }
                            }
                        }

                        // 城市
                        ExposedDropdownMenuBox(
                            expanded = uiState.cityExpanded,
                            onExpandedChange = { viewModel.updateCityExpanded(it) }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedCity,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("城市") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.cityExpanded) },
                                modifier = Modifier.weight(1f)
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.cityExpanded,
                                onDismissRequest = { viewModel.updateCityExpanded(false) }
                            ) {
                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            viewModel.updateCity(city)
                                            viewModel.updateCityExpanded(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 专业模式额外参数
            if (uiState.isProMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("专业参数", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        // 用电行业
                        ExposedDropdownMenuBox(
                            expanded = uiState.industryExpanded,
                            onExpandedChange = { viewModel.updateIndustryExpanded(it) }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedIndustry?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("用电行业") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.industryExpanded) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.industryExpanded,
                                onDismissRequest = { viewModel.updateIndustryExpanded(false) }
                            ) {
                                IndustryType.values().forEach { industry ->
                                    DropdownMenuItem(
                                        text = { Text("${industry.label}（${industry.monthlyUsageKwh}kWh/月）") },
                                        onClick = {
                                            viewModel.updateIndustry(industry)
                                            viewModel.updateIndustryExpanded(false)
                                        }
                                    )
                                }
                            }
                        }

                        // 用电类型
                        Text("用电类型", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ElectricityType.values().forEach { type ->
                                val selected = uiState.selectedElectricityType == type
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateElectricityType(type) },
                                    label = { Text(type.label) }
                                )
                            }
                        }

                        // 峰谷平电价
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.peakPrice,
                                onValueChange = { viewModel.updatePeakPrice(it) },
                                label = { Text("峰时电价") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = uiState.flatPrice,
                                onValueChange = { viewModel.updateFlatPrice(it) },
                                label = { Text("平时电价") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = uiState.valleyPrice,
                                onValueChange = { viewModel.updateValleyPrice(it) },
                                label = { Text("谷时电价") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // 发电模式
                        Text("发电模式", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GenerationMode.values().forEach { mode ->
                                val selected = uiState.selectedGenerationMode == mode
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateGenerationMode(mode) },
                                    label = { Text(mode.label) }
                                )
                            }
                        }

                        // 组件功率
                        ExposedDropdownMenuBox(
                            expanded = uiState.panelPowerExpanded,
                            onExpandedChange = { viewModel.updatePanelPowerExpanded(it) }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedPanelPower?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("组件功率") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.panelPowerExpanded) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.panelPowerExpanded,
                                onDismissRequest = { viewModel.updatePanelPowerExpanded(false) }
                            ) {
                                PanelPower.values().forEach { power ->
                                    DropdownMenuItem(
                                        text = { Text(power.label) },
                                        onClick = {
                                            viewModel.updatePanelPower(power)
                                            viewModel.updatePanelPowerExpanded(false)
                                        }
                                    )
                                }
                            }
                        }

                        // 倾角和系统效率
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.tiltAngle,
                                onValueChange = { viewModel.updateTiltAngle(it) },
                                label = { Text("安装倾角（°）") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = uiState.systemEfficiency,
                                onValueChange = { viewModel.updateSystemEfficiency(it) },
                                label = { Text("系统效率（%）") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // 一键测算按钮
            Button(
                onClick = {
                    val result = viewModel.calculate()
                    if (result != null) {
                        viewModel.saveCalculation(result)
                        Toast.makeText(context, "测算完成", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "请填写完整参数", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("一键测算", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // 测算结果
            uiState.result?.let { result ->
                ResultCards(
                    result = result,
                    isLandscape = isLandscape,
                    onGeneratePdf = { viewModel.generatePdf(context, result) },
                    onSaveCustomer = { /* Navigate to customer save */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ModeSwitcher(isProMode: Boolean, onModeChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = !isProMode,
            onClick = { onModeChange(false) },
            label = { Text("简易测算") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = isProMode,
            onClick = { onModeChange(true) },
            label = { Text("专业测算") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResultCards(
    result: CalculationResult,
    isLandscape: Boolean,
    onGeneratePdf: () -> Unit,
    onSaveCustomer: () -> Unit
) {
    // 核心指标卡片
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "测算结果",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 装机信息
            ResultSectionTitle("装机信息")
            ResultGrid(
                items = listOf(
                    "装机容量" to "${String.format("%.1f", result.installedCapacity)} kW",
                    "组件数量" to "${result.panelCount} 块",
                    "日均发电" to "${String.format("%.1f", result.dailyGeneration)} kWh",
                    "月均发电" to "${String.format("%.0f", result.monthlyGeneration)} kWh",
                    "年发电量" to "${String.format("%.0f", result.yearlyGeneration)} kWh",
                    "25年总发电" to "${String.format("%.0f", result.total25yGeneration)} 万kWh"
                ),
                isLandscape = isLandscape
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 投资收益
            ResultSectionTitle("投资收益")
            ResultGrid(
                items = listOf(
                    "项目总投资" to "¥${String.format("%.2f", result.totalInvestment)}",
                    "月节电收益" to "¥${String.format("%.2f", result.monthlySaving)}",
                    "年节电收益" to "¥${String.format("%.2f", result.yearlySaving)}",
                    "25年总收益" to "¥${String.format("%.2f", result.total25ySaving)}",
                    "静态回本" to "${String.format("%.1f", result.staticPayback)} 年",
                    "动态回本" to "${String.format("%.1f", result.dynamicPayback)} 年"
                ),
                isLandscape = isLandscape,
                highlightColor = AccentGreen
            )

            // 工厂特有指标
            if (result.coverageRatio > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ResultSectionTitle("工厂用电分析")
                ResultGrid(
                    items = listOf(
                        "用电覆盖" to "${String.format("%.1f", result.coverageRatio)}%",
                        "节电率" to "${String.format("%.1f", result.savingRate)}%"
                    ),
                    isLandscape = isLandscape
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 环保数据
            ResultSectionTitle("环保贡献")
            ResultGrid(
                items = listOf(
                    "碳减排量" to "${String.format("%.1f", result.carbonReduction)} 吨CO₂",
                    "等效植树" to "${String.format("%.0f", result.treesEquivalent)} 棵"
                ),
                isLandscape = isLandscape,
                highlightColor = AccentGreen
            )
        }
    }

    // 25年收益曲线
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "25年收益趋势",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            YearlyChartPreview(result.yearlyData)
        }
    }

    // 操作按钮
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onGeneratePdf,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("生成PDF报告")
        }
        Button(
            onClick = onSaveCustomer,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("保存客户")
        }
    }
}

@Composable
fun ResultSectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun ResultGrid(items: List<Pair<String, String>>, isLandscape: Boolean, highlightColor: Color? = null) {
    val columns = if (isLandscape) 3 else 2
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowItems.forEach { (label, value) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(
                            value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = highlightColor ?: TextPrimary
                        )
                    }
                }
                // 填充空位
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun YearlyChartPreview(yearlyData: List<com.pv.sales.model.YearlyData>) {
    // 简易柱状图预览（Compose 原生绘制）
    if (yearlyData.isEmpty()) return

    val maxSaving = yearlyData.maxOfOrNull { it.cumulativeSaving } ?: 1.0
    val barCount = yearlyData.size

    Column {
        yearlyData.forEachIndexed { index, data ->
            if (index % 5 == 0) { // 每5年显示一个
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "第${data.year}年",
                        modifier = Modifier.width(50.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE0E0E0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(data.cumulativeSaving / maxSaving)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GradientStart, GradientEnd)
                                    )
                                )
                        )
                    }
                    Text(
                        "¥${(data.cumulativeSaving / 10000).toInt()}万",
                        modifier = Modifier.width(70.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
