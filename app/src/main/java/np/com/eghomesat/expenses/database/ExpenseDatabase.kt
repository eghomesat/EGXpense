package np.com.eghomesat.expenses.database

import androidx.room.Database
import androidx.room.RoomDatabase
import np.com.eghomesat.expenses.data.Category
import np.com.eghomesat.expenses.data.Expense

@Database(
    entities = [Expense::class, Category::class],
    version = 3,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
}
