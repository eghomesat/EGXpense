package np.com.eghomesat.expenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import np.com.eghomesat.expenses.di.AppModule
import np.com.eghomesat.expenses.navigation.NavGraph
import np.com.eghomesat.expenses.ui.theme.EGExpensesV2Theme
import np.com.eghomesat.expenses.utils.DataStoreManager
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModelFactory
import np.com.eghomesat.expenses.viewmodel.RecoveryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(
            AppModule.provideRepository(this),
            DataStoreManager(this),
            this
        )
    }
    
    private val recoveryViewModel: RecoveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            EGExpensesV2Theme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            recoveryViewModel = recoveryViewModel
                        )
                    }
                }
            }
        }
    }
}
