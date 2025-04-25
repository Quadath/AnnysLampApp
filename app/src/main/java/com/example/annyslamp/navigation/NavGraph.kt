package com.example.annyslamp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Connection : Screen("connection")
}

@Composable
fun AppNavGraph(navController: NavHostController) {

}