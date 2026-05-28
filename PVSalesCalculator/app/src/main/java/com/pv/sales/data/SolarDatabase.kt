package com.pv.sales.data

import com.pv.sales.data.enums.ElectricityType
import com.pv.sales.data.enums.IndustryType

/**
 * 光伏内置数据库 - 离线可用
 * 包含：全国省市日照小时数、峰谷平电价、行业用电量
 */
object SolarDatabase {

    // 峰谷平电价数据
    data class ElectricityPrice(
        val peakPrice: Double,    // 峰时电价 元/kWh
        val flatPrice: Double,    // 平时电价 元/kWh
        val valleyPrice: Double,  // 谷时电价 元/kWh
        val feedInPrice: Double   // 上网电价 元/kWh
    )

    // 城市日照数据
    data class CitySolarData(
        val province: String,
        val city: String,
        val annualSunHours: Double,   // 年等效利用小时数
        val residentialPrice: ElectricityPrice,
        val commercialPrice: ElectricityPrice,
        val largeIndustryPrice: ElectricityPrice
    )

    // 全国主要城市日照与电价数据（基于国家发改委公布数据）
    val cityData: List<CitySolarData> = listOf(
        // 北京
        CitySolarData("北京", "北京", 1200.0,
            ElectricityPrice(0.5283, 0.4983, 0.3183, 0.3932),
            ElectricityPrice(1.3452, 0.8345, 0.3848, 0.3932),
            ElectricityPrice(1.2341, 0.7634, 0.3521, 0.3932)
        ),
        // 上海
        CitySolarData("上海", "上海", 1100.0,
            ElectricityPrice(0.6170, 0.6170, 0.3070, 0.4159),
            ElectricityPrice(1.2640, 0.8040, 0.4270, 0.4159),
            ElectricityPrice(1.1520, 0.7230, 0.3950, 0.4159)
        ),
        // 广东
        CitySolarData("广东", "广州", 1300.0,
            ElectricityPrice(0.6882, 0.6382, 0.3382, 0.4530),
            ElectricityPrice(1.2034, 0.7834, 0.3634, 0.4530),
            ElectricityPrice(1.0923, 0.7023, 0.3312, 0.4530)
        ),
        CitySolarData("广东", "深圳", 1350.0,
            ElectricityPrice(0.6882, 0.6382, 0.3382, 0.4530),
            ElectricityPrice(1.2034, 0.7834, 0.3634, 0.4530),
            ElectricityPrice(1.0923, 0.7023, 0.3312, 0.4530)
        ),
        CitySolarData("广东", "东莞", 1280.0,
            ElectricityPrice(0.6882, 0.6382, 0.3382, 0.4530),
            ElectricityPrice(1.2034, 0.7834, 0.3634, 0.4530),
            ElectricityPrice(1.0923, 0.7023, 0.3312, 0.4530)
        ),
        CitySolarData("广东", "佛山", 1250.0,
            ElectricityPrice(0.6882, 0.6382, 0.3382, 0.4530),
            ElectricityPrice(1.2034, 0.7834, 0.3634, 0.4530),
            ElectricityPrice(1.0923, 0.7023, 0.3312, 0.4530)
        ),
        // 浙江
        CitySolarData("浙江", "杭州", 1150.0,
            ElectricityPrice(0.5880, 0.5380, 0.2880, 0.4153),
            ElectricityPrice(1.1523, 0.7523, 0.3523, 0.4153),
            ElectricityPrice(1.0412, 0.6712, 0.3201, 0.4153)
        ),
        CitySolarData("浙江", "宁波", 1180.0,
            ElectricityPrice(0.5880, 0.5380, 0.2880, 0.4153),
            ElectricityPrice(1.1523, 0.7523, 0.3523, 0.4153),
            ElectricityPrice(1.0412, 0.6712, 0.3201, 0.4153)
        ),
        CitySolarData("浙江", "温州", 1120.0,
            ElectricityPrice(0.5880, 0.5380, 0.2880, 0.4153),
            ElectricityPrice(1.1523, 0.7523, 0.3523, 0.4153),
            ElectricityPrice(1.0412, 0.6712, 0.3201, 0.4153)
        ),
        // 江苏
        CitySolarData("江苏", "南京", 1200.0,
            ElectricityPrice(0.5783, 0.5283, 0.2783, 0.3910),
            ElectricityPrice(1.1423, 0.7423, 0.3423, 0.3910),
            ElectricityPrice(1.0312, 0.6612, 0.3101, 0.3910)
        ),
        CitySolarData("江苏", "苏州", 1220.0,
            ElectricityPrice(0.5783, 0.5283, 0.2783, 0.3910),
            ElectricityPrice(1.1423, 0.7423, 0.3423, 0.3910),
            ElectricityPrice(1.0312, 0.6612, 0.3101, 0.3910)
        ),
        CitySolarData("江苏", "无锡", 1210.0,
            ElectricityPrice(0.5783, 0.5283, 0.2783, 0.3910),
            ElectricityPrice(1.1423, 0.7423, 0.3423, 0.3910),
            ElectricityPrice(1.0312, 0.6612, 0.3101, 0.3910)
        ),
        // 山东
        CitySolarData("山东", "济南", 1350.0,
            ElectricityPrice(0.5769, 0.5469, 0.3769, 0.3949),
            ElectricityPrice(1.1834, 0.7634, 0.3634, 0.3949),
            ElectricityPrice(1.0723, 0.6823, 0.3312, 0.3949)
        ),
        CitySolarData("山东", "青岛", 1300.0,
            ElectricityPrice(0.5769, 0.5469, 0.3769, 0.3949),
            ElectricityPrice(1.1834, 0.7634, 0.3634, 0.3949),
            ElectricityPrice(1.0723, 0.6823, 0.3312, 0.3949)
        ),
        CitySolarData("山东", "烟台", 1320.0,
            ElectricityPrice(0.5769, 0.5469, 0.3769, 0.3949),
            ElectricityPrice(1.1834, 0.7634, 0.3634, 0.3949),
            ElectricityPrice(1.0723, 0.6823, 0.3312, 0.3949)
        ),
        // 四川
        CitySolarData("四川", "成都", 1050.0,
            ElectricityPrice(0.5224, 0.5224, 0.2754, 0.4012),
            ElectricityPrice(1.0923, 0.7023, 0.3323, 0.4012),
            ElectricityPrice(0.9812, 0.6212, 0.3001, 0.4012)
        ),
        // 河南
        CitySolarData("河南", "郑州", 1250.0,
            ElectricityPrice(0.5680, 0.5680, 0.3080, 0.3779),
            ElectricityPrice(1.1323, 0.7323, 0.3423, 0.3779),
            ElectricityPrice(1.0212, 0.6512, 0.3101, 0.3779)
        ),
        CitySolarData("河南", "洛阳", 1280.0,
            ElectricityPrice(0.5680, 0.5680, 0.3080, 0.3779),
            ElectricityPrice(1.1323, 0.7323, 0.3423, 0.3779),
            ElectricityPrice(1.0212, 0.6512, 0.3101, 0.3779)
        ),
        // 湖北
        CitySolarData("湖北", "武汉", 1150.0,
            ElectricityPrice(0.5580, 0.5580, 0.2980, 0.4161),
            ElectricityPrice(1.1223, 0.7223, 0.3323, 0.4161),
            ElectricityPrice(1.0112, 0.6412, 0.3001, 0.4161)
        ),
        // 湖南
        CitySolarData("湖南", "长沙", 1100.0,
            ElectricityPrice(0.5880, 0.5880, 0.3180, 0.4500),
            ElectricityPrice(1.1423, 0.7423, 0.3423, 0.4500),
            ElectricityPrice(1.0312, 0.6612, 0.3101, 0.4500)
        ),
        // 福建
        CitySolarData("福建", "福州", 1200.0,
            ElectricityPrice(0.5280, 0.5280, 0.2780, 0.3932),
            ElectricityPrice(1.0823, 0.7023, 0.3223, 0.3932),
            ElectricityPrice(0.9712, 0.6212, 0.2901, 0.3932)
        ),
        CitySolarData("福建", "厦门", 1250.0,
            ElectricityPrice(0.5280, 0.5280, 0.2780, 0.3932),
            ElectricityPrice(1.0823, 0.7023, 0.3223, 0.3932),
            ElectricityPrice(0.9712, 0.6212, 0.2901, 0.3932)
        ),
        // 安徽
        CitySolarData("安徽", "合肥", 1200.0,
            ElectricityPrice(0.5653, 0.5653, 0.3153, 0.3844),
            ElectricityPrice(1.1323, 0.7323, 0.3423, 0.3844),
            ElectricityPrice(1.0212, 0.6512, 0.3101, 0.3844)
        ),
        // 河北
        CitySolarData("河北", "石家庄", 1300.0,
            ElectricityPrice(0.5200, 0.5200, 0.2800, 0.3684),
            ElectricityPrice(1.0823, 0.7023, 0.3323, 0.3684),
            ElectricityPrice(0.9712, 0.6212, 0.3001, 0.3684)
        ),
        CitySolarData("河北", "保定", 1280.0,
            ElectricityPrice(0.5200, 0.5200, 0.2800, 0.3684),
            ElectricityPrice(1.0823, 0.7023, 0.3323, 0.3684),
            ElectricityPrice(0.9712, 0.6212, 0.3001, 0.3684)
        ),
        // 辽宁
        CitySolarData("辽宁", "沈阳", 1350.0,
            ElectricityPrice(0.5000, 0.5000, 0.2700, 0.3749),
            ElectricityPrice(1.0523, 0.6823, 0.3223, 0.3749),
            ElectricityPrice(0.9412, 0.6012, 0.2901, 0.3749)
        ),
        CitySolarData("辽宁", "大连", 1300.0,
            ElectricityPrice(0.5000, 0.5000, 0.2700, 0.3749),
            ElectricityPrice(1.0523, 0.6823, 0.3223, 0.3749),
            ElectricityPrice(0.9412, 0.6012, 0.2901, 0.3749)
        ),
        // 陕西
        CitySolarData("陕西", "西安", 1250.0,
            ElectricityPrice(0.4983, 0.4983, 0.2683, 0.3545),
            ElectricityPrice(1.0423, 0.6723, 0.3123, 0.3545),
            ElectricityPrice(0.9312, 0.5912, 0.2801, 0.3545)
        ),
        // 山西
        CitySolarData("山西", "太原", 1400.0,
            ElectricityPrice(0.4770, 0.4770, 0.2570, 0.3320),
            ElectricityPrice(1.0223, 0.6623, 0.3023, 0.3320),
            ElectricityPrice(0.9112, 0.5812, 0.2701, 0.3320)
        ),
        // 云南
        CitySolarData("云南", "昆明", 1400.0,
            ElectricityPrice(0.5000, 0.5000, 0.2700, 0.2341),
            ElectricityPrice(1.0523, 0.6823, 0.3223, 0.2341),
            ElectricityPrice(0.9412, 0.6012, 0.2901, 0.2341)
        ),
        // 海南
        CitySolarData("海南", "海口", 1500.0,
            ElectricityPrice(0.6083, 0.6083, 0.3283, 0.4298),
            ElectricityPrice(1.1623, 0.7623, 0.3623, 0.4298),
            ElectricityPrice(1.0512, 0.6812, 0.3301, 0.4298)
        ),
        // 广西
        CitySolarData("广西", "南宁", 1200.0,
            ElectricityPrice(0.5283, 0.5283, 0.2883, 0.4207),
            ElectricityPrice(1.0823, 0.7023, 0.3323, 0.4207),
            ElectricityPrice(0.9712, 0.6212, 0.3001, 0.4207)
        ),
        // 江西
        CitySolarData("江西", "南昌", 1150.0,
            ElectricityPrice(0.6000, 0.6000, 0.3200, 0.4143),
            ElectricityPrice(1.1523, 0.7523, 0.3523, 0.4143),
            ElectricityPrice(1.0412, 0.6712, 0.3201, 0.4143)
        ),
        // 贵州
        CitySolarData("贵州", "贵阳", 1000.0,
            ElectricityPrice(0.4550, 0.4550, 0.2550, 0.3515),
            ElectricityPrice(1.0023, 0.6523, 0.3023, 0.3515),
            ElectricityPrice(0.8912, 0.5712, 0.2701, 0.3515)
        ),
        // 甘肃
        CitySolarData("甘肃", "兰州", 1600.0,
            ElectricityPrice(0.5100, 0.5100, 0.2800, 0.2817),
            ElectricityPrice(1.0623, 0.6923, 0.3323, 0.2817),
            ElectricityPrice(0.9512, 0.6112, 0.3001, 0.2817)
        ),
        // 新疆
        CitySolarData("新疆", "乌鲁木齐", 1500.0,
            ElectricityPrice(0.5000, 0.5000, 0.2700, 0.2600),
            ElectricityPrice(1.0523, 0.6823, 0.3223, 0.2600),
            ElectricityPrice(0.9412, 0.6012, 0.2901, 0.2600)
        ),
        // 内蒙古
        CitySolarData("内蒙古", "呼和浩特", 1600.0,
            ElectricityPrice(0.4650, 0.4650, 0.2650, 0.2829),
            ElectricityPrice(1.0123, 0.6623, 0.3123, 0.2829),
            ElectricityPrice(0.9012, 0.5812, 0.2801, 0.2829)
        ),
        // 宁夏
        CitySolarData("宁夏", "银川", 1650.0,
            ElectricityPrice(0.4600, 0.4600, 0.2600, 0.2595),
            ElectricityPrice(1.0023, 0.6523, 0.3023, 0.2595),
            ElectricityPrice(0.8912, 0.5712, 0.2701, 0.2595)
        ),
        // 青海
        CitySolarData("青海", "西宁", 1700.0,
            ElectricityPrice(0.4550, 0.4550, 0.2550, 0.2277),
            ElectricityPrice(0.9923, 0.6423, 0.2923, 0.2277),
            ElectricityPrice(0.8812, 0.5612, 0.2601, 0.2277)
        ),
        // 西藏
        CitySolarData("西藏", "拉萨", 1800.0,
            ElectricityPrice(0.5000, 0.5000, 0.2700, 0.2500),
            ElectricityPrice(1.0523, 0.6823, 0.3223, 0.2500),
            ElectricityPrice(0.9412, 0.6012, 0.2901, 0.2500)
        ),
        // 吉林
        CitySolarData("吉林", "长春", 1400.0,
            ElectricityPrice(0.5250, 0.5250, 0.2850, 0.3635),
            ElectricityPrice(1.0723, 0.6923, 0.3323, 0.3635),
            ElectricityPrice(0.9612, 0.6112, 0.3001, 0.3635)
        ),
        // 黑龙江
        CitySolarData("黑龙江", "哈尔滨", 1350.0,
            ElectricityPrice(0.5100, 0.5100, 0.2800, 0.3740),
            ElectricityPrice(1.0623, 0.6923, 0.3323, 0.3740),
            ElectricityPrice(0.9512, 0.6112, 0.3001, 0.3740)
        ),
        // 重庆
        CitySolarData("重庆", "重庆", 950.0,
            ElectricityPrice(0.5200, 0.5200, 0.2800, 0.3964),
            ElectricityPrice(1.0723, 0.6923, 0.3323, 0.3964),
            ElectricityPrice(0.9612, 0.6112, 0.3001, 0.3964)
        ),
        // 天津
        CitySolarData("天津", "天津", 1250.0,
            ElectricityPrice(0.4900, 0.4900, 0.2700, 0.3655),
            ElectricityPrice(1.0423, 0.6723, 0.3223, 0.3655),
            ElectricityPrice(0.9312, 0.5912, 0.2901, 0.3655)
        ),
        // 香港
        CitySolarData("香港", "香港", 1300.0,
            ElectricityPrice(1.2000, 1.2000, 0.8000, 0.5000),
            ElectricityPrice(1.5000, 1.2000, 0.9000, 0.5000),
            ElectricityPrice(1.4000, 1.1000, 0.8500, 0.5000)
        )
    )

    // 获取所有省份列表
    fun getProvinces(): List<String> = cityData.map { it.province }.distinct().sorted()

    // 获取指定省份的城市列表
    fun getCities(province: String): List<String> =
        cityData.filter { it.province == province }.map { it.city }.sorted()

    // 获取城市日照数据
    fun getCitySolarData(province: String, city: String): CitySolarData? =
        cityData.find { it.province == province && it.city == city }

    // 获取电价（根据用电类型）
    fun getElectricityPrice(
        province: String,
        city: String,
        type: ElectricityType
    ): ElectricityPrice? {
        val data = getCitySolarData(province, city) ?: return null
        return when (type) {
            ElectricityType.RESIDENTIAL -> data.residentialPrice
            ElectricityType.GENERAL_COMMERCIAL -> data.commercialPrice
            ElectricityType.LARGE_INDUSTRY -> data.largeIndustryPrice
        }
    }

    // 获取行业月用电量
    fun getIndustryMonthlyUsage(industry: IndustryType): Int = industry.monthlyUsageKwh

    // 组件价格库（元/W）
    data class PanelPricing(
        val pricePerWatt: Double,  // 元/W
        val efficiency: Double     // 组件效率
    )

    val panelPricing = mapOf(
        450 to PanelPricing(1.05, 0.205),
        500 to PanelPricing(1.00, 0.213),
        550 to PanelPricing(0.98, 0.221)
    )

    // 逆变器价格（元/kW）
    const val INVERTER_PRICE_PER_KW = 0.35

    // 安装辅材价格（元/W）
    const val INSTALLATION_COST_PER_WATT = 0.50

    // EPC综合单价（元/W）- 含设计、施工、并网
    const val EPC_PRICE_PER_WATT = 0.30

    // 碳排放因子：每kWh发电量减排 0.785 kg CO₂ (中国电网平均因子)
    const val CARBON_FACTOR_KG_PER_KWH = 0.785

    // 每棵树年吸收CO₂约 18.3 kg
    const val TREE_ABSORPTION_KG_PER_YEAR = 18.3

    // 系统衰减率（每年）
    const val ANNUAL_DEGRADATION_RATE = 0.005 // 0.5%

    // 折现率（用于动态回本周期计算）
    const val DISCOUNT_RATE = 0.06 // 6%
}
