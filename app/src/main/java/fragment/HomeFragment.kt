package fragment

import Adapter.RecyclerAdapter
import Database.FoodDatabase
import Database.FoodEntity
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codiinggeek.food.R
import model.Food
import org.json.JSONException
import util.ConnectionManager
import java.util.*
import java.util.Arrays.sort
import java.util.Collections.sort
import kotlin.Comparator

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment (val contextParam: Context): Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var radioButtonView:View

    lateinit var recyclerAdapter: RecyclerAdapter
    val foodInfoList = arrayListOf<Food>()

    var ratingComparator= Comparator<Food> { rest1, rest2 ->

        if(rest1.foodRating.compareTo(rest2.foodRating,true)==0){
            rest1.foodName.compareTo(rest2.foodName,true)
        }
        else{
            rest1.foodRating.compareTo(rest2.foodRating,true)
        }

    }

    var costComparator= Comparator<Food> { rest1, rest2 ->

        rest1.foodPrice.compareTo(rest2.foodPrice,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET,
            url, null, Response.Listener {
                try{
                    progressLayout.visibility =View.GONE
                    val data1 = it.getJSONObject("data")
                    val success = data1.getBoolean("success")
                    if(success){
                        val data = data1.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val foodJsonObject =data.getJSONObject(i)
                            val foodObject = Food(
                                foodJsonObject.getString("id").toInt(),
                                foodJsonObject.getString("name"),
                                foodJsonObject.getString("rating"),
                                foodJsonObject.getString("cost_for_one"),
                                foodJsonObject.getString("image_url")
                            )
                            foodInfoList.add(foodObject)

                            recyclerAdapter = RecyclerAdapter(activity as Context, foodInfoList)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                            recyclerHome.itemAnimator = DefaultItemAnimator()
                            recyclerHome.setHasFixedSize(true)
                        }
                    }else {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(activity as Context, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                }
                },Response.ErrorListener {
                    if(activity!=null)
                        Toast.makeText(activity as Context, "Volley Error occurred", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "919c7039d0ab7d"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure")
            dialog.setMessage("No Internet Connection Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {

            val id = item.itemId
            when(id) {

                R.id.radio_high_to_low -> {
                    Collections.sort(foodInfoList, costComparator)
                    foodInfoList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                }
                R.id.radio_low_to_high -> {
                    Collections.sort(foodInfoList, costComparator)
                    recyclerAdapter.notifyDataSetChanged()
                }
                R.id.radio_rating -> {
                    Collections.sort(foodInfoList, ratingComparator)
                    foodInfoList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

        return super.onOptionsItemSelected(item)
    }

}
