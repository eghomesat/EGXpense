package np.com.eghomesat.expenses.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import np.com.eghomesat.expenses.data.Category
import np.com.eghomesat.expenses.data.Expense
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AppBackup(
    val expenses: List<Expense>,
    val categories: List<Category>,
    val timestamp: Long = System.currentTimeMillis()
)

object BackupUtils {

    fun createBackup(context: Context, expenses: List<Expense>, categories: List<Category>) {
        try {
            val backup = AppBackup(expenses, categories)
            val jsonString = Json.encodeToString(backup)
            val fileName = "EGExpenses_Backup_${System.currentTimeMillis()}.json"
            
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = uri?.let { resolver.openOutputStream(it) }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadsDir, fileName)
                outputStream = java.io.FileOutputStream(file)
            }

            outputStream?.use {
                it.write(jsonString.toByteArray())
                Toast.makeText(context, "Backup saved to Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreBackup(context: Context, inputStream: InputStream, onRestore: (AppBackup) -> Unit) {
        try {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val backup = Json.decodeFromString<AppBackup>(jsonString)
            onRestore(backup)
            Toast.makeText(context, "Data restored successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Restore failed: Invalid file", Toast.LENGTH_SHORT).show()
        }
    }
}
