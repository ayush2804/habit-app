package com.ayushpandey.habitapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ayushpandey.habitapp.HabitApplication

object Routes {
    const val HOME = "home"
    const val INSIGHTS = "insights"
    const val DETAIL = "detail/{habitId}"
    fun detail(id: Long) = "detail/$id"
}

@Composable
fun HabitNav(app: HabitApplication) {
    val navController = rememberNavController()
    val homeVm: HabitViewModel = viewModel(factory = HabitViewModel.factory(app))

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HabitListScreen(
                viewModel = homeVm,
                onOpenInsights = { navController.navigate(Routes.INSIGHTS) },
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) }
            )
        }
        composable(Routes.INSIGHTS) {
            InsightsScreen(
                viewModel = homeVm,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { entry ->
            val habitId = entry.arguments?.getLong("habitId") ?: return@composable
            val detailVm: HabitDetailViewModel = viewModel(
                factory = HabitDetailViewModel.factory(app, habitId)
            )
            HabitDetailScreen(
                viewModel = detailVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
