package com.example.egexpensesv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.egexpensesv2.screens.AddExpenseScreen
import com.example.egexpensesv2.screens.HomeScreen
import com.example.egexpensesv2.screens.ReportsScreen
import com.example.egexpensesv2.screens.ViewExpensesScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {

        composable(AppScreen.Home.route) {
            HomeScreen(navController)
        }

        composable(AppScreen.AddExpense.route) {
            AddExpenseScreen(navController)
        }

        composable(AppScreen.Reports.route) {
            ReportsScreen(navController)
        }

        composable(AppScreen.ViewExpenses.route) {
            ViewExpensesScreen(navController)
        }
    }
}