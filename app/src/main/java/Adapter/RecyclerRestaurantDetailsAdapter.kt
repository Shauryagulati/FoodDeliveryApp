package Adapter

import Database.MenuDatabase
import Database.MenuEntity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.codiinggeek.food.Cart
import com.codiinggeek.food.R
import model.RestaurantMenu

class RecyclerRestaurantDetailsAdapter(val context: Context, val itemList: ArrayList<RestaurantMenu>, var btnCart: Button):
    RecyclerView.Adapter<RecyclerRestaurantDetailsAdapter.RestaurantDetailsViewHolder>(){

    var count:Int=0
    var itemsSelectedId= arrayListOf<String>()
    var itemsPrice= arrayListOf<Int>()
    var itemsName = arrayListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).
                inflate(R.layout.recycler_single_restaurant_details, parent, false)
        return RestaurantDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val menu = itemList[position]
       // var id = menu.DishId
        holder.txtDishId.text = (position+1).toString()
        holder.txtDishName.text = menu.DishName
        holder.txtDishPrice.text = menu.DishPrice.toString()

        val listOfFav= GetAllFavAsyncTask(context).execute().get()

        if(listOfFav.isNotEmpty()&&listOfFav.contains(menu.DishId.toString())){
            val favColor = ContextCompat.getColor(
                context.applicationContext,
                R.color.colorSecondary
            )
            holder.btnDishAdd.text = "REMOVE"
            holder.btnDishAdd.setBackgroundColor(favColor)
        }

        holder.btnDishAdd.setOnClickListener {
            val menuEntity = MenuEntity(
                menu.DishId,
                menu.DishName,
                menu.DishPrice
            )

            if(!DBAsyncTask(context, menuEntity, 1).execute().get()){
                val async = DBAsyncTask(context, menuEntity, 2).execute()
                val result = async.get()
                if (result){
                    val favColor = ContextCompat.getColor(
                        context.applicationContext,
                        R.color.colorSecondary
                    )
                    holder.btnDishAdd.text = "REMOVE"
                    holder.btnDishAdd.setBackgroundColor(favColor)
                    count++;
                    itemsSelectedId.add(menu.DishId.toString())
                    itemsPrice.add(menu.DishPrice)
                    itemsName.add(menu.DishName)
                }
            }else{
                val async = DBAsyncTask(context, menuEntity, 3).execute()
                val result = async.get()
                if (result){
                    val favColor = ContextCompat.getColor(
                        context.applicationContext,
                        R.color.colorPrimary
                    )
                    holder.btnDishAdd.text = "ADD"
                    holder.btnDishAdd.setBackgroundColor(favColor)
                    count--;
                    itemsSelectedId.remove(menu.DishId.toString())
                    itemsPrice.remove(menu.DishPrice)
                    itemsName.remove(menu.DishName)
                }
            }
            if (count>0){
                btnCart.visibility = View.VISIBLE
            }else{
                btnCart.visibility = View.GONE
            }
        }
        btnCart.setOnClickListener {
            val intent= Intent(context, Cart::class.java)

            intent.putExtra("selectedItemId",itemsSelectedId)
            intent.putExtra("itemPrice", itemsPrice)
            intent.putExtra("itemName", itemsName)

            context.startActivity(intent)
        }
    }

    class RestaurantDetailsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtDishId : TextView = view.findViewById(R.id.txtDishId)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
        val btnDishAdd: Button = view.findViewById(R.id.btnDishAdd)
    }



    class DBAsyncTask(context: Context, val menuEntity: MenuEntity, val mode: Int):
        AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, MenuDatabase:: class.java, "res1-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1 ->{
                    val res:MenuEntity? =
                        db.menuDao().getMenuById(menuEntity.dish_id.toString())
                        db.close()
                    return res!=null
                }
                2 ->{
                    db.menuDao().insertMenu(menuEntity)
                    db.close()
                    return true
                }
                3 ->{
                    db.menuDao().deleteMenu(menuEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
    class GetAllFavAsyncTask(val context: Context) :
        AsyncTask<Void, Void, List<String>>(){
        val db =Room.databaseBuilder(context, MenuDatabase::class.java, "res1-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val list =db.menuDao().getAllMenu()
            val listOfId = arrayListOf<String>()
            for(i in list){
                listOfId.add(i.dish_id.toString())
            }
            return listOfId
        }

    }

}