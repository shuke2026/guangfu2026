package com.pv.sales.model

/**
 * 测算结果数据模型
 */
data class CalculationResult(
    // 基础参数
    val roofArea: Double,
    val scene: String,
    val province: String,
    val city: String,
    val generationMode: String = "SELF_USE",
    val electricityType: String = "RESIDENTIAL",
    val industryType: String = "",
    val panelPower: Int = 550,
    val systemEfficiency: Double = 0.82,
    val tiltAngle: Double = 25.0,

    // 装机参数
    val installedCapacity: Double = 0.0,    // kW
    val panelCount: Int = 0,                // 块

    // 发电量
    val dailyGeneration: Double = 0.0,      // kWh
    val monthlyGeneration: Double = 0.0,    // kWh
    val yearlyGeneration: Double = 0.0,     // kWh
    val total25yGeneration: Double = 0.0,   // kWh

    // 投资
    val totalInvestment: Double = 0.0,      // 元

    // 收益
    val monthlySaving: Double = 0.0,        // 元
    val yearlySaving: Double = 0.0,         // 元
    val total25ySaving: Double = 0.0,       // 元

    // 回本周期
    val staticPayback: Double = 0.0,        // 年
    val dynamicPayback: Double = 0.0,       // 年

    // 工厂特有
    val coverageRatio: Double = 0.0,        // %
    val savingRate: Double = 0.0,           // %

    // 环保
    val carbonReduction: Double = 0.0,      // 吨 CO₂
    val treesEquivalent: Double = 0.0,      // 棵

    // 25年逐年数据（用于图表）
    val yearlyData: List<YearlyData> = emptyList()
)

data class YearlyData(
    val year: Int,              // 第N年
    val generation: Double,     // 当年发电量 kWh
    val saving: Double,        // 当年收益 元
    val cumulativeSaving: Double // 累计收益 元
)
