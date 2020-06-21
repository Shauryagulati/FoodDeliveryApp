package fragment

import Adapter.RecyclerRestaurantDetailsAdapter
import Database.MenuDatabase
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codiinggeek.food.MainActivity
import com.codiinggeek.food.R
import model.RestaurantMenu
import org.json.JSONException
import util.ConnectionManager


/**
 * A simple [Fragment] subclass.
 */
class RestaurantDetails : Fragment() {

    lateinit var recyclerRestaurantDetails: RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnCart: Button
    var rest_id:String? = "-1"
    var rest_name: String? = ""

    lateinit var recyclerRestaurantDetailsAdapter: RecyclerRestaurantDetailsAdapter
    val restaurantMenu = arrayListOf<RestaurantMenu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_details, container, false)

        val sharedPreferences = context?.getSharedPreferences("Menu Data", Context.MODE_PRIVATE)

        if(sharedPreferences !=null){
            rest_id = sharedPreferences.getString("RestaurantId","-1")
            rest_name = sharedPreferences.getString("RestaurantName","")
        }else{
            activity?.finish()
            Toast.makeText(activity, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        if(rest_id=="-1"){
            activity?.finish()
            Toast.makeText(activity, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        recyclerRestaurantDetails = view.findViewById(R.id.recyclerRestaurantDetails)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        btnCart = view.findViewById(R.id.btnCart)
        btnCart.visibility = View.GONE

        val queue =Volley.newRequestQueue(activity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$rest_id"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonRequest = object: JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    try{
                        progressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if(success){
                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restJsonObject = data.getJSONObject(i)
                                val restObject = RestaurantMenu(
                                    restJsonObject.getString("id").toInt(),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("cost_for_one").toInt(),
                                    restJsonObject.getString("restaurant_id")
                                )
                                restaurantMenu.add(restObject)
                                recyclerRestaurantDetailsAdapter = RecyclerRestaurantDetailsAdapter(activity as Context, restaurantMenu, btnCart)
                                recyclerRestaurantDetails.adapter = recyclerRestaurantDetailsAdapter
                                recyclerRestaurantDetails.layoutManager = layoutManager
                                recyclerRestaurantDetails.itemAnimator = DefaultItemAnimator()
                                recyclerRestaurantDetails.setHasFixedSize(true)

                            }
                        }else{
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(activity as Context, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity,
                        "Volley Error $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "919c7039d0ab7d"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }else {
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

    class EmptyPrevious(context: Context):AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, MenuDatabase:: class.java, "res1-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.menuDao().clearMenu()
            db.close()
            return true
        }
    }

}
