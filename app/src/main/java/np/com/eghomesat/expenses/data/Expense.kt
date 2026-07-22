package np.com.eghomesat.expenses.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "expenses")
@Serializable
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val bsDate: String,

    val category: String,

    val amount: Double,

    val remarks: String,

    val time: String = ""
)