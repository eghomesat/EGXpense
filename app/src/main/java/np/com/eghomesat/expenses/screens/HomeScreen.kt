package np.com.eghomesat.expenses.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.eghomesat.expenses.navigation.AppScreen
import np.com.eghomesat.expenses.ui.theme.*
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val lockoutUntil by viewModel.lockoutUntil
    val totalExpense = expenses.sumOf { it.amount }

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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "EGExpenses",
            style = MaterialTheme.typography.headlineSmall, // Reduced size
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Personal Expense Tracker",
            style = MaterialTheme.typography.bodyMedium, // Consistent hierarchy
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Developed By: Deepak Devkota",
            color = DangerRed,
            style = MaterialTheme.typography.labelMedium, // Smaller and not bold
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "(9808201020)",
            color = Color.Black, // Match subtitle
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Total Expenses Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MainBlue)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Column {
                    Text(
                        text = "Total Expenses",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPrivacyMode) "Rs. ******" else "Rs. $totalExpense",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onEyeClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Privacy",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // COMPACT ACTION BUTTONS (Single Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactActionButton(Icons.Default.Add, "Add") { navController.navigate(AppScreen.AddExpense.route) }
            CompactActionButton(Icons.Default.List, "View") { navController.navigate(AppScreen.ViewExpenses.route) }
            CompactActionButton(Icons.Default.BarChart, "Reports") { navController.navigate(AppScreen.Reports.route) }
            CompactActionButton(Icons.Default.Category, "Category") { navController.navigate(AppScreen.CategoryManagement.route) }
            CompactActionButton(Icons.Default.Settings, "Settings") { navController.navigate(AppScreen.Settings.route) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Expenses Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Expenses",
                style = MaterialTheme.typography.titleSmall, // Compact and elegant
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(
                onClick = onEyeClick,
                modifier = Modifier.size(24.dp) // Match text size
            ) {
                Icon(
                    if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recent Expenses List
        expenses.take(5).forEach { expense ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = expense.category,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = if (isPrivacyMode) "****" else "Rs. ${expense.amount}",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MainBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = expense.time.ifEmpty { "[00:00am]" },
                        modifier = Modifier.padding(start = 4.dp),
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isPrivacyMode) Color.Gray else MainBlue
                    )
                }
            }
        }
    }

    // PIN Dialogs...
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

@Composable
fun CompactActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MainBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
