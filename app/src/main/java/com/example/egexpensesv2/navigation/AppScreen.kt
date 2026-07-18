package com.example.egexpensesv2.navigation

sealed class AppScreen(val route: String) {

    object Home : AppScreen("home")

    object AddExpense : AppScreen("add_expense")

    object Reports : AppScreen("reports")

    object ViewExpenses : AppScreen("view_expenses")
}