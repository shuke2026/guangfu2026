package com.pv.sales.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.pv.sales.ui.components.BottomNavBar
import com.pv.sales.ui.navigation.NavGraph
import com.pv.sales.ui.theme.PVSalesTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PVSalesTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
