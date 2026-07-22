package np.com.eghomesat.expenses.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.eghomesat.expenses.data.Expense
import np.com.eghomesat.expenses.navigation.AppScreen
import np.com.eghomesat.expenses.ui.theme.*
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewExpensesScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val lockoutUntil by viewModel.lockoutUntil

    var searchQuery by remember { mutableStateOf("") }
    
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    var showPinDialog by remember { mutableStateOf(false) }
    var showWrongPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    
    val isLockedOut = lockoutUntil > System.currentTimeMillis()

    val onEyeClick = {
        if (isPrivacyMode) {
            if (isPinEnabled) {
                showPinDialog = true
            } else {
                viewModel.togglePrivacyMode()
            }
        } else {
            viewModel.togglePrivacyMode()
        }
    }

    val filteredExpenses = if (searchQuery.isEmpty()) {
        expenses
    } else {
        expenses.filter { 
            it.category.contains(searchQuery, ignoreCase = true) || 
            it.remarks.contains(searchQuery, ignoreCase = true) 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Expenses", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEyeClick) {
                        Icon(
                            if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Privacy",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainBlue)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by category or remarks...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainBlue,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredExpenses) { expense ->
                    ExpenseCard(
                        expense = expense,
                        isPrivacyMode = isPrivacyMode,
                        onEdit = { expenseToEdit = expense },
                        onDelete = { expenseToDelete = expense }
                    )
                }
            }
        }

        // Dialogs...
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showPinDialog = false
                    pinInput = ""
                },
                title = { Text(if (isLockedOut) "Security Lockout" else "Enter PIN to Show Amounts") },
                text = {
                    Column {
                        if (isLockedOut) {
                            Text("Too many failed attempts. Please wait before trying again.", color = MaterialTheme.colorScheme.error)
                        } else {
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { if (it.length <= 4) pinInput = it },
                                label = { Text("4-Digit PIN") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    if (!isLockedOut) {
                        TextButton(onClick = {
                            if (viewModel.verifyPin(pinInput)) {
                                viewModel.togglePrivacyMode()
                                showPinDialog = false
                                pinInput = ""
                            } else {
                                showPinDialog = false
                                pinInput = ""
                                showWrongPinDialog = true
                            }
                        }) {
                            Text("VERIFY")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showPinDialog = false
                        pinInput = ""
                    }) {
                        Text("CANCEL")
                    }
                }
            )
        }

        if (showWrongPinDialog) {
            AlertDialog(
                onDismissRequest = { showWrongPinDialog = false },
                title = { Text("Error") },
                text = { Text("Your PIN is wrong. Retype again.") },
                confirmButton = {
                    TextButton(onClick = { 
                        showWrongPinDialog = false
                        showPinDialog = true 
                    }) {
                        Text("RETYPE")
                    }
                }
            )
        }

        // Edit Confirmation Dialog
        if (expenseToEdit != null) {
            AlertDialog(
                onDismissRequest = { expenseToEdit = null },
                title = { Text("Confirm Edit") },
                text = { Text("Do you want to edit this expense?") },
                confirmButton = {
                    TextButton(onClick = {
                        val expenseId = expenseToEdit?.id ?: -1
                        expenseToEdit = null
                        navController.navigate(AppScreen.editExpense(expenseId))
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expenseToEdit = null }) {
                        Text("No")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = { expenseToDelete = null },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this expense?") },
                confirmButton = {
                    TextButton(onClick = {
                        expenseToDelete?.let { viewModel.deleteExpense(it) }
                        expenseToDelete = null
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expenseToDelete = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    isPrivacyMode: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Date : ${expense.bsDate}", fontSize = 13.sp)
                Text(text = expense.time, fontSize = 13.sp, color = TextGrey)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Category : ${expense.category}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Amount : Rs. ${if (isPrivacyMode) "****" else expense.amount}", fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Remarks : ${expense.remarks}", fontSize = 13.sp)
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .height(30.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MainBlue),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MainBlue),
                        width = 1.dp
                    )
                ) {
                    Text("Edit", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DangerRed),
                        width = 1.dp
                    )
                ) {
                    Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}
