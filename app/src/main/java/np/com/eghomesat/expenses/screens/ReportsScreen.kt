package np.com.eghomesat.expenses.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import np.com.eghomesat.expenses.ui.theme.*
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val lockoutUntil by viewModel.lockoutUntil
    
    val months = expenses.map { it.bsDate.substring(0, 7) }.distinct().sortedDescending()
    var selectedMonth by remember { mutableStateOf(if (months.isNotEmpty()) months.first() else "") }
    var showMonthMenu by remember { mutableStateOf(false) }

    val filteredExpenses = if (selectedMonth.isEmpty()) expenses else expenses.filter { it.bsDate.startsWith(selectedMonth) }
    val categoryTotals = filteredExpenses.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    val totalExpense = categoryTotals.values.sum()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Reports",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { showMonthMenu = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MainBlue)
            ) {
                Text(if (selectedMonth.isEmpty()) "Select Month" else "Month: $selectedMonth", fontSize = 16.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = showMonthMenu,
                onDismissRequest = { showMonthMenu = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            selectedMonth = month
                            showMonthMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Expenses Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Expenses",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    IconButton(onClick = onEyeClick) {
                        Icon(
                            if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isPrivacyMode) "****" else "Rs. $totalExpense",
                    color = MainBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Breakdown by Category",
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            categoryTotals.forEach { (category, total) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = if (isPrivacyMode) "****" else "Rs. $total",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(0.6f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
        ) {
            Text("Back", fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }

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
}
