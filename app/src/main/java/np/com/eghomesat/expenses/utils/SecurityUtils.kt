package np.com.eghomesat.expenses.utils

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.*

object SecurityUtils {
    private const val TAG = "SecurityUtils"
    private const val PREFS_NAME = "secure_prefs"
    private const val INSTALLATION_ID_KEY = "installation_id"
    private const val PIN_KEY = "secure_user_pin"
    private const val PHONE_KEY = "verified_phone_number"
    private const val DEVICE_ID_KEY = "verified_device_id"

    private fun getEncryptedPrefs(context: Context) = try {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.e(TAG, "Failed to create EncryptedSharedPreferences", e)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getInstallationId(context: Context): String {
        val prefs = getEncryptedPrefs(context)
        var id = prefs.getString(INSTALLATION_ID_KEY, null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString(INSTALLATION_ID_KEY, id).apply()
        }
        return id
    }

    fun saveSecurePin(context: Context, pin: String) {
        getEncryptedPrefs(context).edit().putString(PIN_KEY, pin).apply()
    }

    fun getSecurePin(context: Context): String? {
        return getEncryptedPrefs(context).getString(PIN_KEY, null)
    }

    fun deleteSecurePin(context: Context) {
        getEncryptedPrefs(context).edit().remove(PIN_KEY).apply()
    }

    fun saveVerifiedPhone(context: Context, phone: String) {
        getEncryptedPrefs(context).edit().putString(PHONE_KEY, phone).apply()
    }

    fun getVerifiedPhone(context: Context): String? {
        return getEncryptedPrefs(context).getString(PHONE_KEY, null)
    }

    fun saveBoundDeviceId(context: Context, id: String) {
        getEncryptedPrefs(context).edit().putString(DEVICE_ID_KEY, id).apply()
    }

    fun getBoundDeviceId(context: Context): String? {
        return getEncryptedPrefs(context).getString(DEVICE_ID_KEY, null)
    }

    fun isWeakPin(pin: String): Boolean {
        val commonPins = listOf("1111", "1234", "0000", "9876", "1212", "2222", "4321")
        return pin in commonPins || pin.length < 4
    }

    fun maskPhoneNumber(phone: String): String {
        if (phone.length < 10) return phone
        val prefix = phone.take(4) // e.g. +977
        val suffix = phone.takeLast(2)
        return "$prefix-******$suffix"
    }
}
