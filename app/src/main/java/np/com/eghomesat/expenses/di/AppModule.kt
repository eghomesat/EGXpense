package np.com.eghomesat.expenses.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import np.com.eghomesat.expenses.data.Category
import np.com.eghomesat.expenses.database.ExpenseDatabase
import np.com.eghomesat.expenses.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppModule {

    @Volatile
    private var database: ExpenseDatabase? = null

    fun provideRepository(context: Context): ExpenseRepository {
        val db = database ?: synchronized(this) {
            var instance: ExpenseDatabase? = null
            instance = Room.databaseBuilder(
                context.applicationContext,
                ExpenseDatabase::class.java,
                "eg_expenses_db"
            )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        instance?.let { checkAndSeedCategories(it) }
                    }
                }
            })
            .build()

            database = instance
            instance
        }

        return ExpenseRepository(db.expenseDao(), db.categoryDao())
    }

    private suspend fun checkAndSeedCategories(db: ExpenseDatabase) {
        val count = db.categoryDao().getCount()
        if (count == 0) {
            val categories = listOf(
                "House Expenses",
                "Food & Beverage",
                "Meat (Chicken-Mutton)",
                "Fuel (Petrol & Diesel)",
                "Transportation",
                "Vehicle Maintenance",
                "Mobile Recharge",
                "Internet Bill",
                "Electricity Bill",
                "Water Khanepani Bill",
                "Hospital (Medicines)",
                "Educational Fee",
                "Travel (Entertainment)",
                "Office Expenses",
                "Business Expenses",
                "Loan EMI (Interest)",
                "Savings (Insurance)",
                "Expenses (Insurance)",
                "Miscellaneous (Others)"
            )
            categories.forEach { name ->
                db.categoryDao().insertCategory(Category(name = name))
            }
        }
    }
}
