package com.example.kyohei.healthapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kyohei.healthapp.data.AppDatabase
import com.example.kyohei.healthapp.data.HealthRepository
import com.example.kyohei.healthapp.ui.HealthViewModel
import com.example.kyohei.healthapp.ui.HealthViewModelFactory
import com.example.kyohei.healthapp.ui.screens.GraphScreen
import com.example.kyohei.healthapp.ui.screens.RecordScreen
import com.example.kyohei.healthapp.ui.screens.SettingsScreen
import com.example.kyohei.healthapp.ui.theme.HealthAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val heightPreferences = com.example.kyohei.healthapp.data.HeightPreferences(this)
        val repository = HealthRepository(database.healthDao(), heightPreferences)
        val factory = HealthViewModelFactory(repository)

        setContent {
            HealthAppTheme {
                MainScreen(factory)
            }
        }
    }
}

sealed class Screen(
    val route: String, 
    val title: String, 
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Record : Screen("record", "記録", Icons.Filled.List, Icons.Outlined.List)
    object Graph : Screen("graph", "グラフ", Icons.Filled.ShowChart, Icons.Outlined.ShowChart)
    object Settings : Screen("settings", "設定", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainScreen(factory: HealthViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: HealthViewModel = viewModel(factory = factory)
    
    val items = listOf(Screen.Record, Screen.Graph, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (isSelected) screen.selectedIcon else screen.unselectedIcon, 
                                contentDescription = screen.title
                            ) 
                        },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelMedium) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Record.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(Screen.Record.route) { RecordScreen(viewModel) }
            composable(Screen.Graph.route) { GraphScreen(viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
        }
    }
}