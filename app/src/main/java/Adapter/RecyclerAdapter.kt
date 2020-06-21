package Adapter

import Database.FoodDatabase
import Database.FoodEntity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codiinggeek.food.R
import com.squareup.picasso.Picasso
import fragment.RestaurantDetails
import model.Food
import fragment.*

class RecyclerAdapter(val context: Context, val itemList: ArrayList<Food>):
    RecyclerView.Adapter<RecyclerAdapter.HomeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).
                inflate(R.layout.recycler_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food= itemList[position]

        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = food.foodPrice
        holder.txtRating.text = food.foodRating
        Picasso.get().load(food.foodImage).error(R.drawable.default_food_pic).into(holder.imgFoodImage)

        val listOfFav= GetAllFavAsyncTask(context).execute().get()

        if(listOfFav.isNotEmpty()&&listOfFav.contains(food.foodId.toString())){
            holder.btnFavHeart.setImageResource(R.drawable.ic_red_heart)
        }else{
            holder.btnFavHeart.setImageResource(R.drawable.ic_blank_heart)
        }

        holder.btnFavHeart.setOnClickListener{
            val foodEntity =FoodEntity(
                food.foodId,
                food.foodName,
                food.foodRating,
                food.foodPrice,
                food.foodImage
            )
            if(!DBAsyncTask(context, foodEntity, 1).execute().get()){
                val async = DBAsyncTask(context, foodEntity, 2).execute()
                val result =async.get()
                if(result){
                    holder.btnFavHeart.setImageResource(R.drawable.ic_red_heart)
                }
            }else{
                val async = DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if(result){
                    holder.btnFavHeart.setImageResource(R.drawable.ic_blank_heart)
                }
            }
        }

        holder.cardRestaurant.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences(
                "Menu Data", Context.MODE_PRIVATE
            )
            sharedPreferences.edit().putString("RestaurantId", food.foodId.toString()).apply()
            sharedPreferences.edit().putString("RestaurantName", food.foodName).apply()
            val activity : AppCompatActivity = context as AppCompatActivity
            activity.supportActionBar?.title = food.foodName
            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.frame,
                    RestaurantDetails()
                )
                .commit()
        }
    }
    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){

        val cardRestaurant: CardView = view.findViewById(R.id.cardRestaurant)
        val btnFavHeart: ImageView = view.findViewById(R.id.btnFavHeart)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

    class DBAsyncTask(context: Context, val foodEntity: FoodEntity, val mode: Int):
            AsyncTask<Void, Void, Boolean>(){
            val db = Room.databaseBuilder(context, FoodDatabase:: class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1 ->{
                    val res:FoodEntity? =
                        db.foodDao().getFoodById(foodEntity.food_id.toString())
                     db.close()
                    return res!=null
                }
                2 ->{
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true
                }
                3 ->{
                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
    class GetAllFavAsyncTask(
        context: Context
    ):
            AsyncTask<Void, Void, List<String>>(){
        val db =Room.databaseBuilder(context, FoodDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val list =db.foodDao().getAllFood()
            val listOfId = arrayListOf<String>()
            for(i in list){
                listOfId.add(i.food_id.toString())
            }
            return listOfId
        }

    }

}