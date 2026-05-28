package com.pv.sales.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Customers : Screen("customers")
    object Reports : Screen("reports")
    object Profile : Screen("profile")
    object CalculationDetail : Screen("calculation_detail/{recordId}") {
        fun createRoute(recordId: Long) = "calculation_detail/$recordId"
    }
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
}
