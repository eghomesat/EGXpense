package com.example.egexpensesv2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val bsDate: String,

    val category: String,

    val amount: Double,

    val remarks: String
)