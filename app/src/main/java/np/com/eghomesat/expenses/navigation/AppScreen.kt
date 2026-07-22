package np.com.eghomesat.expenses.navigation

sealed class AppScreen(val route: String) {

    object Home : AppScreen("home")

    object AddExpense : AppScreen("add_expense")

    object Reports : AppScreen("reports")

    object ViewExpenses : AppScreen("view_expenses")

    object CategoryManagement : AppScreen("category_management")

    object Settings : AppScreen("settings")

    object VerifyPhone : AppScreen("verify_phone")

    object SetupNewPin : AppScreen("setup_new_pin")

    companion object {
        fun editExpense(id: Int) = "edit_expense/$id"
    }
}
