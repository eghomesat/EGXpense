package np.com.eghomesat.expenses.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import np.com.eghomesat.expenses.utils.SecurityUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

sealed class RecoveryState {
    object Idle : RecoveryState()
    object Loading : RecoveryState()
    data class CodeSent(val verificationId: String) : RecoveryState()
    object Verified : RecoveryState()
    data class DeviceWarning(val storedId: String, val currentId: String) : RecoveryState()
    data class Error(val message: String) : RecoveryState()
}

class RecoveryViewModel : ViewModel() {
    private var verificationId: String? = null

    private val _state = MutableStateFlow<RecoveryState>(RecoveryState.Idle)
    val state: StateFlow<RecoveryState> = _state

    /**
     * Safely retrieves the FirebaseAuth instance.
     * Logs technical errors and returns null if initialization fails.
     */
    private fun getFirebaseAuth(context: Context): FirebaseAuth? {
        return try {
            // Attempt to initialize if it somehow missed automatic initialization
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("RecoveryViewModel", "Firebase initialization failure: ${e.message}", e)
            null
        }
    }

    fun sendOtp(phoneNumber: String, activity: Activity) {
        val auth = getFirebaseAuth(activity.applicationContext) ?: run {
            _state.value = RecoveryState.Error("Unable to verify your identity at the moment. Please try again later.")
            return
        }
        
        _state.value = RecoveryState.Loading
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential, activity.applicationContext)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("RecoveryViewModel", "Phone verification failed: ${e.message}", e)
                    _state.value = RecoveryState.Error("Verification failed. Please check your phone number and try again.")
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    _state.value = RecoveryState.CodeSent(id)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String, context: Context) {
        val id = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithCredential(credential, context)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, context: Context) {
        val auth = getFirebaseAuth(context) ?: return
        _state.value = RecoveryState.Loading
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val verifiedPhone = auth.currentUser?.phoneNumber
                    val currentId = SecurityUtils.getInstallationId(context)
                    val storedId = SecurityUtils.getBoundDeviceId(context)

                    if (verifiedPhone != null) {
                        SecurityUtils.saveVerifiedPhone(context, verifiedPhone)
                    }
                    
                    if (storedId != null && storedId != currentId) {
                        _state.value = RecoveryState.DeviceWarning(storedId, currentId)
                    } else {
                        if (storedId == null) {
                            SecurityUtils.saveBoundDeviceId(context, currentId)
                        }
                        _state.value = RecoveryState.Verified
                    }
                } else {
                    Log.e("RecoveryViewModel", "Credential sign-in failed: ${task.exception?.message}")
                    _state.value = RecoveryState.Error("Invalid code. Please try again.")
                }
            }
    }

    fun resetState() {
        _state.value = RecoveryState.Idle
    }
}
