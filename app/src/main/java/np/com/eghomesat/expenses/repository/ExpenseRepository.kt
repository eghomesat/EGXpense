package np.com.eghomesat.expenses.repository

import np.com.eghomesat.expenses.data.Category
import np.com.eghomesat.expenses.data.Expense
import np.com.eghomesat.expenses.database.CategoryDao
import np.com.eghomesat.expenses.database.CategorySummary
import np.com.eghomesat.expenses.database.ExpenseDao
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {

    val allExpenses: Flow<List<Expense>> =
        expenseDao.getAllExpenses()

    val categorySummaries: Flow<List<CategorySummary>> =
        expenseDao.getCategorySummaries()

    val allCategories: Flow<List<Category>> =
        categoryDao.getAllCategories()

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
}
