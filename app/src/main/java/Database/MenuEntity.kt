package Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class MenuEntity (
    @PrimaryKey val dish_id: Int,
    @ColumnInfo(name = "dish_name") val dish_name: String,
    @ColumnInfo(name = "dish_price") val dish_price: Int
)