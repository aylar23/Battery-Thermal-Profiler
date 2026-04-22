package com.aylar.batterythermalprofiler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aylar.batterythermalprofiler.ui.theme.BatteryThermalProfilerTheme
import com.aylar.batterythermalprofiler.navigation.AppNavHost
import com.aylar.batterythermalprofiler.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryThermalProfilerTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
private fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route == Routes.Dashboard,
                    onClick = { navController.navigate(Routes.Dashboard) },
                    label = { Text("Dashboard") },
                    icon = {},
                )
                NavigationBarItem(
                    selected = route == Routes.Apps,
                    onClick = { navController.navigate(Routes.Apps) },
                    label = { Text("Apps") },
                    icon = {},
                )
                NavigationBarItem(
                    selected = route == Routes.Trends,
                    onClick = { navController.navigate(Routes.Trends) },
                    label = { Text("Trends") },
                    icon = {},
                )
                NavigationBarItem(
                    selected = route == Routes.Report,
                    onClick = { navController.navigate(Routes.Report) },
                    label = { Text("Report") },
                    icon = {},
                )
            }
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BatteryThermalProfilerTheme {
        AppScaffold()
    }
}