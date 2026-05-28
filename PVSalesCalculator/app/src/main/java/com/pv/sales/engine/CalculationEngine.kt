package com.pv.sales.engine

import com.pv.sales.data.SolarDatabase
import com.pv.sales.data.enums.*
import com.pv.sales.model.CalculationResult
import com.pv.sales.model.YearlyData
import kotlin.math.pow

/**
 * 光伏测算引擎
 * 遵循 GB/T 国家标准
 *
 * 核心算法：
 * 1. 装机容量 = 屋顶面积 × 装机密度（户用0.6kW/㎡, 工商业0.7kW/㎡, 工厂0.8kW/㎡）
 * 2. 年发电量 = 装机容量 × 年等效利用小时数 × 系统效率
 * 3. 收益按峰谷平电价 + 发电模式精准拆分
 * 4. 静态回本 = 总投资 / 年收益
 * 5. 动态回本 = 考虑资金折现（折现率6%）
 * 6. 碳减排 = 总发电量 × 0.785 kg/kWh
 */
object CalculationEngine {

    // 装机密度 kW/㎡（根据安装场景）
    private fun getInstallDensity(scene: InstallScene): Double {
        return when (scene) {
            InstallScene.RESIDENTIAL -> 0.60  // 户用屋顶，考虑间距和遮挡
            InstallScene.COMMERCIAL -> 0.70  // 工商业彩钢瓦屋顶
            InstallScene.FACTORY -> 0.80     // 工厂大面积水泥屋顶
        }
    }

    /**
     * 执行完整测算
     */
    fun calculate(
        roofArea: Double,
        scene: InstallScene,
        province: String,
        city: String,
        generationMode: GenerationMode = GenerationMode.SELF_USE,
        electricityType: ElectricityType = ElectricityType.RESIDENTIAL,
        industryType: IndustryType? = null,
        panelPower: PanelPower = PanelPower.W550,
        systemEfficiency: Double = 0.82,
        tiltAngle: Double = 25.0,
        customPeakPrice: Double? = null,
        customFlatPrice: Double? = null,
        customValleyPrice: Double? = null
    ): CalculationResult? {
        // 参数校验
        if (roofArea <= 0 || province.isBlank() || city.isBlank()) return null

        // 获取城市数据
        val cityData = SolarDatabase.getCitySolarData(province, city) ?: return null
        val electricityPrice = SolarDatabase.getElectricityPrice(province, city, electricityType) ?: return null

        // 1. 计算装机容量
        val installDensity = getInstallDensity(scene)
        val installedCapacity = roofArea * installDensity // kW

        // 2. 计算组件数量
        val panelCount = (installedCapacity * 1000 / panelPower.watts).toInt()

        // 3. 计算发电量
        val annualSunHours = cityData.annualSunHours
        val yearlyGeneration = installedCapacity * annualSunHours * systemEfficiency // kWh
        val monthlyGeneration = yearlyGeneration / 12.0
        val dailyGeneration = yearlyGeneration / 365.0

        // 4. 计算总投资
        val panelPricing = SolarDatabase.panelPricing[panelPower.watts]
        val panelCost = installedCapacity * 1000 * (panelPricing?.pricePerWatt ?: 1.0)
        val inverterCost = installedCapacity * SolarDatabase.INVERTER_PRICE_PER_KW * 1000
        val installationCost = installedCapacity * 1000 * SolarDatabase.INSTALLATION_COST_PER_WATT
        val epcCost = installedCapacity * 1000 * SolarDatabase.EPC_PRICE_PER_WATT
        val totalInvestment = panelCost + inverterCost + installationCost + epcCost

        // 5. 计算收益
        val peakPrice = customPeakPrice ?: electricityPrice.peakPrice
        val flatPrice = customFlatPrice ?: electricityPrice.flatPrice
        val valleyPrice = customValleyPrice ?: electricityPrice.valleyPrice
        val feedInPrice = electricityPrice.feedInPrice

        // 综合电价（峰谷平加权平均，按行业用电时段比例）
        val weightedPrice = when (electricityType) {
            ElectricityType.RESIDENTIAL -> flatPrice // 居民单一电价
            ElectricityType.GENERAL_COMMERCIAL -> peakPrice * 0.4 + flatPrice * 0.35 + valleyPrice * 0.25
            ElectricityType.LARGE_INDUSTRY -> peakPrice * 0.35 + flatPrice * 0.35 + valleyPrice * 0.30
        }

        val (yearlySaving, total25ySaving) = when (generationMode) {
            GenerationMode.FULL_GRID -> {
                // 全额上网：所有发电量按上网电价结算
                val yearly = yearlyGeneration * feedInPrice
                val total = calculate25YearGeneration(yearlyGeneration, feedInPrice, 0.0)
                Pair(yearly, total)
            }
            GenerationMode.SELF_USE -> {
                // 自发自用：所有发电量按综合电价节省
                val yearly = yearlyGeneration * weightedPrice
                val total = calculate25YearGeneration(yearlyGeneration, weightedPrice, 0.0)
                Pair(yearly, total)
            }
            GenerationMode.SURPLUS_GRID -> {
                // 余电上网：80%自用 + 20%上网
                val selfUseRatio = 0.80
                val gridRatio = 0.20
                val yearly = yearlyGeneration * (selfUseRatio * weightedPrice + gridRatio * feedInPrice)
                val total = calculate25YearGeneration(
                    yearlyGeneration,
                    selfUseRatio * weightedPrice + gridRatio * feedInPrice,
                    0.0
                )
                Pair(yearly, total)
            }
        }

        val monthlySaving = yearlySaving / 12.0

        // 6. 回本周期
        val staticPayback = if (yearlySaving > 0) totalInvestment / yearlySaving else Double.MAX_VALUE
        val dynamicPayback = calculateDynamicPayback(totalInvestment, yearlyGeneration, weightedPrice, feedInPrice, generationMode)

        // 7. 工厂用电分析
        var coverageRatio = 0.0
        var savingRate = 0.0
        if (scene == InstallScene.FACTORY && industryType != null) {
            val monthlyUsage = industryType.monthlyUsageKwh.toDouble()
            coverageRatio = (monthlyGeneration / monthlyUsage) * 100.0
            savingRate = (monthlySaving / (monthlyUsage * weightedPrice)) * 100.0
        }

        // 8. 环保数据
        val total25yGeneration = calculate25YearTotalGeneration(yearlyGeneration)
        val carbonReduction = total25yGeneration * SolarDatabase.CARBON_FACTOR_KG_PER_KWH / 1000.0 // 吨
        val treesEquivalent = carbonReduction * 1000.0 / (SolarDatabase.TREE_ABSORPTION_KG_PER_YEAR * 25) // 棵

        // 9. 25年逐年数据
        val yearlyData = generateYearlyData(yearlyGeneration, weightedPrice, feedInPrice, generationMode)

        return CalculationResult(
            roofArea = roofArea,
            scene = scene.name,
            province = province,
            city = city,
            generationMode = generationMode.name,
            electricityType = electricityType.name,
            industryType = industryType?.name ?: "",
            panelPower = panelPower.watts,
            systemEfficiency = systemEfficiency,
            tiltAngle = tiltAngle,
            installedCapacity = installedCapacity,
            panelCount = panelCount,
            dailyGeneration = dailyGeneration,
            monthlyGeneration = monthlyGeneration,
            yearlyGeneration = yearlyGeneration,
            total25yGeneration = total25yGeneration,
            totalInvestment = totalInvestment,
            monthlySaving = monthlySaving,
            yearlySaving = yearlySaving,
            total25ySaving = total25ySaving,
            staticPayback = staticPayback,
            dynamicPayback = dynamicPayback,
            coverageRatio = coverageRatio,
            savingRate = savingRate,
            carbonReduction = carbonReduction,
            treesEquivalent = treesEquivalent,
            yearlyData = yearlyData
        )
    }

    /**
     * 计算25年总发电量（考虑系统衰减）
     * 首年不衰减，之后每年衰减0.5%
     */
    private fun calculate25YearTotalGeneration(firstYearGeneration: Double): Double {
        var total = 0.0
        for (year in 1..25) {
            val degradation = if (year == 1) 1.0 else (1.0 - SolarDatabase.ANNUAL_DEGRADATION_RATE).pow(year - 1)
            total += firstYearGeneration * degradation
        }
        return total
    }

    /**
     * 计算25年总收益
     */
    private fun calculate25YearGeneration(
        firstYearGeneration: Double,
        pricePerKwh: Double,
        subsidyPerKwh: Double
    ): Double {
        var total = 0.0
        for (year in 1..25) {
            val degradation = if (year == 1) 1.0 else (1.0 - SolarDatabase.ANNUAL_DEGRADATION_RATE).pow(year - 1)
            total += firstYearGeneration * degradation * pricePerKwh
        }
        return total
    }

    /**
     * 计算动态回本周期（考虑资金折现）
     */
    private fun calculateDynamicPayback(
        totalInvestment: Double,
        yearlyGeneration: Double,
        weightedPrice: Double,
        feedInPrice: Double,
        mode: GenerationMode
    ): Double {
        val discountRate = SolarDatabase.DISCOUNT_RATE
        var cumulativeNPV = -totalInvestment
        var paybackYear = 0.0

        for (year in 1..30) {
            val degradation = if (year == 1) 1.0 else (1.0 - SolarDatabase.ANNUAL_DEGRADATION_RATE).pow(year - 1)
            val yearGeneration = yearlyGeneration * degradation

            val yearSaving = when (mode) {
                GenerationMode.FULL_GRID -> yearGeneration * feedInPrice
                GenerationMode.SELF_USE -> yearGeneration * weightedPrice
                GenerationMode.SURPLUS_GRID -> yearGeneration * (0.8 * weightedPrice + 0.2 * feedInPrice)
            }

            val discountedSaving = yearSaving / (1.0 + discountRate).pow(year)
            cumulativeNPV += discountedSaving

            if (cumulativeNPV >= 0) {
                // 线性插值精确计算
                val prevNPV = cumulativeNPV - discountedSaving
                val fraction = -prevNPV / discountedSaving
                paybackYear = (year - 1) + fraction
                break
            }
        }

        return if (paybackYear > 0) paybackYear else 30.0
    }

    /**
     * 生成25年逐年数据（用于图表展示）
     */
    private fun generateYearlyData(
        firstYearGeneration: Double,
        weightedPrice: Double,
        feedInPrice: Double,
        mode: GenerationMode
    ): List<YearlyData> {
        val dataList = mutableListOf<YearlyData>()
        var cumulativeSaving = 0.0

        for (year in 1..25) {
            val degradation = if (year == 1) 1.0 else (1.0 - SolarDatabase.ANNUAL_DEGRADATION_RATE).pow(year - 1)
            val yearGeneration = firstYearGeneration * degradation

            val yearSaving = when (mode) {
                GenerationMode.FULL_GRID -> yearGeneration * feedInPrice
                GenerationMode.SELF_USE -> yearGeneration * weightedPrice
                GenerationMode.SURPLUS_GRID -> yearGeneration * (0.8 * weightedPrice + 0.2 * feedInPrice)
            }

            cumulativeSaving += yearSaving

            dataList.add(
                YearlyData(
                    year = year,
                    generation = yearGeneration,
                    saving = yearSaving,
                    cumulativeSaving = cumulativeSaving
                )
            )
        }

        return dataList
    }
}
