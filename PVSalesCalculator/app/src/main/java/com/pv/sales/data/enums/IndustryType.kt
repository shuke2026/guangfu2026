package com.pv.sales.data.enums

enum class IndustryType(val label: String, val monthlyUsageKwh: Int) {
    MANUFACTURING("制造业", 150000),
    CHEMICAL("化工业", 200000),
    FOOD("食品加工业", 100000),
    METAL("金属加工", 180000),
    TEXTILE("纺织业", 120000),
    ELECTRONICS("电子制造", 160000),
    PHARMACEUTICAL("医药制造", 140000),
    BUILDING_MATERIALS("建材业", 220000),
    PAPER("造纸业", 250000),
    OTHER("其他", 100000)
}
