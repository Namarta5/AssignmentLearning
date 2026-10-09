package com.example.assignmentlearning.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignmentlearning.presentation.course.DashboardScreen
import com.example.assignmentlearning.presentation.details.DetailsScreen
import com.example.assignmentlearning.presentation.login.LoginScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                nav.navigate("dashboard") { popUpTo("login") { inclusive = true } }
            })
        }
        composable("dashboard") { DashboardScreen(btnContinueClick = { nav.navigate("details/$it") }) }
        composable("details/{courseId}", arguments = listOf(navArgument("courseId") { type = NavType.IntType })) {
            DetailsScreen(onBack = { nav.popBackStack() })
        }
    }
}
