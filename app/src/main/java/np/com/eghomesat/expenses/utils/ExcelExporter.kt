package np.com.eghomesat.expenses.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import np.com.eghomesat.expenses.data.Expense
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

object ExcelExporter {

    fun exportExpensesToExcel(context: Context, expenses: List<Expense>) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Expenses")

            // Create Header
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Date (BS)")
            headerRow.createCell(1).setCellValue("Category")
            headerRow.createCell(2).setCellValue("Amount")
            headerRow.createCell(3).setCellValue("Remarks")

            // Create Data Rows
            expenses.forEachIndexed { index, expense ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(expense.bsDate)
                row.createCell(1).setCellValue(expense.category)
                row.createCell(2).setCellValue(expense.amount)
                row.createCell(3).setCellValue(expense.remarks)
            }

            val fileName = "EGExpenses_${System.currentTimeMillis()}.xlsx"
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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
                workbook.write(it)
                Toast.makeText(context, "Excel exported to Downloads", Toast.LENGTH_LONG).show()
            }
            workbook.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
