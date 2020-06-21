package Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuEntity::class], version = 2)
abstract class MenuDatabase: RoomDatabase() {
    abstract fun menuDao():MenuDao
}