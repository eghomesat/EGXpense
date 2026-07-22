package np.com.eghomesat.expenses.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import np.com.eghomesat.expenses.repository.ExpenseRepository
import np.com.eghomesat.expenses.utils.DataStoreManager

class ExpenseViewModelFactory(
    private val repository: ExpenseRepository,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            // Use applicationContext to avoid memory leaks
            return ExpenseViewModel(repository, dataStoreManager, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
