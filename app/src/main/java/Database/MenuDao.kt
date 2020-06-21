package Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {
    @Insert
    fun insertMenu(menuEntity: MenuEntity)

    @Query("DELETE from menu")
    fun clearMenu()

    @Delete
    fun deleteMenu(menuEntity: MenuEntity)

    @Query("SELECT * FROM menu")
    fun getAllMenu(): List<MenuEntity>

    @Query("SELECT * FROM menu WHERE dish_id = :dishId")
    fun getMenuById(dishId: String): MenuEntity
}