package np.com.eghomesat.expenses.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import np.com.eghomesat.expenses.data.Category
import np.com.eghomesat.expenses.data.Expense
import np.com.eghomesat.expenses.repository.ExpenseRepository
import np.com.eghomesat.expenses.utils.DataStoreManager
import np.com.eghomesat.expenses.utils.SecurityUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    init {
        // Ensure categories are seeded if the list is empty
        viewModelScope.launch {
            repository.allCategories.collect { list ->
                if (list.isEmpty()) {
                    seedDefaultCategories()
                }
            }
        }

        // Initialize privacy mode based on whether Master PIN is enabled
        viewModelScope.launch {
            val isEnabled = dataStoreManager.isPinEnabled.first()
            isPrivacyMode.value = isEnabled && SecurityUtils.getSecurePin(context) != null
        }
    }

    private fun seedDefaultCategories() {
        val defaultCategories = listOf(
            "House Expenses", "Food & Beverage", "Meat (Chicken-Mutton)",
            "Fuel (Petrol & Diesel)", "Transportation", "Vehicle Maintenance",
            "Mobile Recharge", "Internet Bill", "Electricity Bill",
            "Water Khanepani Bill", "Hospital (Medicines)", "Educational Fee",
            "Travel (Entertainment)", "Office Expenses", "Business Expenses",
            "Loan EMI (Interest)", "Savings (Insurance)", "Expenses (Insurance)",
            "Miscellaneous (Others)"
        )
        defaultCategories.forEach { name ->
            insertCategory(name)
        }
    }

    val expenses = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val summaries = repository.categorySummaries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var isPrivacyMode = mutableStateOf(true)
        private set

    var failedAttempts = mutableStateOf(0)
        private set
    var lockoutUntil = mutableStateOf(0L)
        private set

    fun togglePrivacyMode() {
        isPrivacyMode.value = !isPrivacyMode.value
    }

    fun verifyPin(enteredPin: String): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime < lockoutUntil.value) return false

        val securePin = SecurityUtils.getSecurePin(context)
        val isCorrect = enteredPin == securePin
        
        if (isCorrect) {
            failedAttempts.value = 0
            lockoutUntil.value = 0
        } else {
            failedAttempts.value += 1
            if (failedAttempts.value >= 5) {
                // Lock for 30 seconds after 5 attempts
                lockoutUntil.value = currentTime + 30000 
            }
        }
        return isCorrect
    }

    // Settings & Security
    // Refine isPinEnabled to strictly reflect both the flag and the presence of a secure PIN
    val isPinEnabled = dataStoreManager.isPinEnabled.map { enabled ->
        enabled && SecurityUtils.getSecurePin(context) != null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    
    val isDarkMode = dataStoreManager.isDarkMode.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setPin(pin: String) {
        viewModelScope.launch {
            SecurityUtils.saveSecurePin(context, pin)
            dataStoreManager.setPinEnabled(true)
        }
    }

    fun disablePin() {
        viewModelScope.launch {
            SecurityUtils.deleteSecurePin(context)
            dataStoreManager.setPinEnabled(false)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDarkMode(enabled)
        }
    }

    // CRUD
    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.update(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun insertCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun restoreData(expenses: List<Expense>, categories: List<Category>) {
        viewModelScope.launch {
            expenses.forEach { repository.insert(it) }
            categories.forEach { repository.insertCategory(it) }
        }
    }
}
