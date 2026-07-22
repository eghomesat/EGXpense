package np.com.eghomesat.expenses.database

import androidx.room.*
import np.com.eghomesat.expenses.data.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY bsDate DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    fun getCategorySummaries(): Flow<List<CategorySummary>>
}

data class CategorySummary(
    val category: String,
    val total: Double
)
