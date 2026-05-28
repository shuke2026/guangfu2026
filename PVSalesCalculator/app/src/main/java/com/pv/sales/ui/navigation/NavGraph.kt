package com.pv.sales.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pv.sales.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Customers.route) {
            CustomerListScreen(navController = navController)
        }
        composable(Screen.Reports.route) {
            ReportListScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(
            route = Screen.CalculationDetail.route,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            CalculationDetailScreen(navController = navController, recordId = recordId)
        }
        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
            CustomerDetailScreen(navController = navController, customerId = customerId)
        }
    }
}
