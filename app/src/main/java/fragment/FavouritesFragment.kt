package fragment

import Adapter.RecyclerAdapter
import Database.FoodDatabase
import Database.FoodEntity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.codiinggeek.food.R
import model.Food

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {
    lateinit var recyclerFavourites: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val foodInfoList = arrayListOf<Food>()
    lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourites = view.findViewById(R.id.recyclerFavourites)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if(backgroundList.isNotEmpty()){
            progressLayout.visibility = View.GONE
            for(i in backgroundList){
                foodInfoList.add(Food(i.food_id,i.foodName,i.foodRating, i.foodPrice,i.foodImage))
            }
            recyclerAdapter = RecyclerAdapter(activity as Context, foodInfoList)
            recyclerFavourites.adapter = recyclerAdapter
            recyclerFavourites.layoutManager = layoutManager
            recyclerFavourites.itemAnimator = DefaultItemAnimator()
            recyclerFavourites.setHasFixedSize(true)
        }else{
            progressLayout.visibility = View.GONE
        }

        return view
    }
    class FavouritesAsync(context: Context): AsyncTask<Void, Void, List<FoodEntity>>(){
    val db = Room.databaseBuilder(context, FoodDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            return db.foodDao().getAllFood()
        }

    }
}
