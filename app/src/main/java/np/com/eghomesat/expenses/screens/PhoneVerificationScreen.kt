package np.com.eghomesat.expenses.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.eghomesat.expenses.navigation.AppScreen
import np.com.eghomesat.expenses.ui.theme.BackgroundLight
import np.com.eghomesat.expenses.ui.theme.MainBlue
import np.com.eghomesat.expenses.utils.SecurityUtils
import np.com.eghomesat.expenses.viewmodel.RecoveryState
import np.com.eghomesat.expenses.viewmodel.RecoveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationScreen(
    navController: NavController,
    viewModel: RecoveryViewModel
) {
    val context = LocalContext.current
    val activity = context as Activity
    val state by viewModel.state.collectAsState()
    
    var phoneNumber by remember { mutableStateOf(SecurityUtils.getVerifiedPhone(context) ?: "") }
    var otpCode by remember { mutableStateOf("") }
    val isRegistered = SecurityUtils.getVerifiedPhone(context) != null

    LaunchedEffect(state) {
        if (state is RecoveryState.Verified) {
            navController.navigate(AppScreen.SetupNewPin.route) {
                popUpTo(AppScreen.VerifyPhone.route) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Identity", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state is RecoveryState.Idle || (state is RecoveryState.Loading && otpCode.isEmpty())) {
                Text(
                    text = if (isRegistered) "Verify your phone number" else "Register phone number",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRegistered) 
                        "We will send an OTP to ${SecurityUtils.maskPhoneNumber(phoneNumber)}" 
                        else "Enter your mobile number to link it for PIN recovery.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number (e.g. +977...)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRegistered,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { viewModel.sendOtp(phoneNumber, activity) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                    enabled = phoneNumber.length >= 10 && state !is RecoveryState.Loading
                ) {
                    if (state is RecoveryState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Send OTP")
                    }
                }
            } else if (state is RecoveryState.CodeSent || (state is RecoveryState.Loading && otpCode.isNotEmpty())) {
                Text(text = "Enter OTP Code", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Sent to $phoneNumber", color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    label = { Text("6-Digit Code") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { viewModel.verifyOtp(otpCode, context) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                    enabled = otpCode.length == 6 && state !is RecoveryState.Loading
                ) {
                    if (state is RecoveryState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Verify & Continue")
                    }
                }
            }
            
            if (state is RecoveryState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = (state as RecoveryState.Error).message, color = MaterialTheme.colorScheme.error)
            }

            if (state is RecoveryState.DeviceWarning) {
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() },
                    title = { Text("Device Mismatch Warning") },
                    text = {
                        Text(
                            "This device does not match the one originally linked to your account.\n\n" +
                            "For your security, PIN recovery on a new device requires additional caution."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { 
                            // User confirmed, proceed to setup new PIN
                            navController.navigate(AppScreen.SetupNewPin.route) {
                                popUpTo(AppScreen.VerifyPhone.route) { inclusive = true }
                            }
                            viewModel.resetState()
                        }) {
                            Text("CONTINUE ANYWAY")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.resetState() }) {
                            Text("CANCEL")
                        }
                    }
                )
            }
        }
    }
}
