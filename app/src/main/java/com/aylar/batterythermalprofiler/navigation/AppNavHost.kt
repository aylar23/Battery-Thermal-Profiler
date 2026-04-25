package com.aylar.batterythermalprofiler.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aylar.batterythermalprofiler.feature.apps.AppsRoute
import com.aylar.batterythermalprofiler.feature.dashboard.DashboardRoute
import com.aylar.batterythermalprofiler.feature.report.ReportRoute
import com.aylar.batterythermalprofiler.feature.trends.TrendsRoute
import com.aylar.batterythermalprofiler.feature.settings.SettingsRoute

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
        composable(Routes.Dashboard) { DashboardRoute() }
        composable(Routes.Apps) { AppsRoute() }
        composable(Routes.Trends) { TrendsRoute() }
        composable(Routes.Report) { ReportRoute() }
        composable(Routes.Settings) { SettingsRoute() }
    }
}

