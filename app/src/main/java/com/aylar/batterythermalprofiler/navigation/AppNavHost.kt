package com.aylar.batterythermalprofiler.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aylar.batterythermalprofiler.feature.apps.AppsScreen
import com.aylar.batterythermalprofiler.feature.dashboard.DashboardScreen
import com.aylar.batterythermalprofiler.feature.report.ReportScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard,
        modifier = modifier,
    ) {
        composable(Routes.Dashboard) { DashboardScreen() }
        composable(Routes.Apps) { AppsScreen() }
        composable(Routes.Report) { ReportScreen() }
    }
}

