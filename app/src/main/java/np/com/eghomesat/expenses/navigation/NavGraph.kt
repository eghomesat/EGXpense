package np.com.eghomesat.expenses.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import np.com.eghomesat.expenses.screens.*
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel
import np.com.eghomesat.expenses.viewmodel.RecoveryViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    recoveryViewModel: RecoveryViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {

        composable(AppScreen.Home.route) {
            HomeScreen(navController, viewModel)
        }

        composable(AppScreen.AddExpense.route) {
            AddExpenseScreen(navController, viewModel)
        }

        composable(
            route = "edit_expense/{expenseId}",
            arguments = listOf(androidx.navigation.navArgument("expenseId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: -1
            AddExpenseScreen(navController, viewModel, expenseId)
        }

        composable(AppScreen.Reports.route) {
            ReportsScreen(navController, viewModel)
        }

        composable(AppScreen.ViewExpenses.route) {
            ViewExpensesScreen(navController, viewModel)
        }

        composable(AppScreen.CategoryManagement.route) {
            CategoryManagementScreen(navController, viewModel)
        }

        composable(AppScreen.Settings.route) {
            SettingsScreen(navController, viewModel)
        }

        composable(AppScreen.VerifyPhone.route) {
            PhoneVerificationScreen(navController, recoveryViewModel)
        }

        composable(AppScreen.SetupNewPin.route) {
            PinResetScreen(navController, viewModel)
        }
    }
}
