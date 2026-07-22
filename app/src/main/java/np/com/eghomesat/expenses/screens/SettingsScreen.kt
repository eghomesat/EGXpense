package np.com.eghomesat.expenses.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import np.com.eghomesat.expenses.ui.theme.BackgroundLight
import np.com.eghomesat.expenses.ui.theme.MainBlue
import np.com.eghomesat.expenses.utils.BackupUtils
import np.com.eghomesat.expenses.utils.ExcelExporter
import np.com.eghomesat.expenses.viewmodel.ExpenseViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: ExpenseViewModel
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                BackupUtils.restoreBackup(context, stream) { backup ->
                    viewModel.restoreData(backup.expenses, backup.categories)
                }
            }
        }
    }

    var showSetPinDialog by remember { mutableStateOf(false) }
    var showVerifyPinDialog by remember { mutableStateOf(false) }
    var showWrongPinDialog by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Security", style = MaterialTheme.typography.titleMedium, color = MainBlue, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                SettingsItem("PIN Protection", Icons.Default.Lock) {
                    if (!isPinEnabled) showSetPinDialog = true
                }
                Switch(
                    checked = isPinEnabled,
                    onCheckedChange = { 
                        if (it) {
                            showSetPinDialog = true
                        } else {
                            showVerifyPinDialog = true
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("Appearance", style = MaterialTheme.typography.titleMedium, color = MainBlue, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Dark Mode", modifier = Modifier.padding(vertical = 16.dp))
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) })
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Data Management", style = MaterialTheme.typography.titleMedium, color = MainBlue, fontWeight = FontWeight.Bold)
            SettingsItem("Export to Excel", Icons.Default.Share) {
                ExcelExporter.exportExpensesToExcel(context, expenses)
            }
            SettingsItem("Backup Data (JSON)", Icons.Default.Storage) {
                BackupUtils.createBackup(context, expenses, categories)
            }
            SettingsItem("Restore Data (JSON)", Icons.Default.Storage) {
                restoreLauncher.launch("application/json")
            }
        }

        // Dialogs...
        if (showSetPinDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showSetPinDialog = false
                    pinText = ""
                },
                title = { Text("Set 4-Digit PIN") },
                text = {
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4) pinText = it },
                        label = { Text("Enter PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (pinText.length == 4) {
                            viewModel.setPin(pinText)
                            pinText = ""
                            showSetPinDialog = false
                        }
                    }) {
                        Text("SAVE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showSetPinDialog = false
                        pinText = ""
                    }) {
                        Text("CANCEL")
                    }
                }
            )
        }

        if (showVerifyPinDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showVerifyPinDialog = false
                    pinText = ""
                },
                title = { Text("Enter PIN to Disable") },
                text = {
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4) pinText = it },
                        label = { Text("Enter PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (viewModel.verifyPin(pinText)) {
                            viewModel.disablePin()
                            pinText = ""
                            showVerifyPinDialog = false
                        } else {
                            showVerifyPinDialog = false
                            pinText = ""
                            showWrongPinDialog = true
                        }
                    }) {
                        Text("VERIFY")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showVerifyPinDialog = false
                        pinText = ""
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
                        showVerifyPinDialog = true 
                    }) {
                        Text("RETYPE")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.wrapContentHeight(),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MainBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
