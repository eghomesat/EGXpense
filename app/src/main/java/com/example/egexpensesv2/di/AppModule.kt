package com.example.egexpensesv2.di

import android.content.Context
import androidx.room.Room
import com.example.egexpensesv2.database.ExpenseDatabase
import com.example.egexpensesv2.repository.ExpenseRepository

object AppModule {

    @Volatile
    private var database: ExpenseDatabase? = null

    fun provideRepository(context: Context): ExpenseRepository {
        val db = database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ExpenseDatabase::class.java,
                "eg_expenses_db"
            ).build()

            database = instance
            instance
        }

        return ExpenseRepository(db.expenseDao())
    }
}