package com.example.egexpensesv2.repository

import com.example.egexpensesv2.data.Expense
import com.example.egexpensesv2.database.ExpenseDao
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val dao: ExpenseDao
) {

    val allExpenses: Flow<List<Expense>> =
        dao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        dao.insertExpense(expense)
    }

    suspend fun update(expense: Expense) {
        dao.updateExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        dao.deleteExpense(expense)
    }
}