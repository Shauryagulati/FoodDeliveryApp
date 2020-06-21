package fragment

import Adapter.OrderHistoryAdapter
import Adapter.RecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.codiinggeek.food.R
import model.OrderHistoryRestaurant
import org.json.JSONException
import util.ConnectionManager
import java.sql.Connection
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var rlNoOrderHistory: RelativeLayout

    lateinit var userId: String

    lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        rlNoOrderHistory = view.findViewById(R.id.rlNoOrderHistory)
        rlNoOrderHistory.visibility = View.GONE

        layoutManager = LinearLayoutManager(activity)

        val orderedRestaurantList=ArrayList<OrderHistoryRestaurant>()

        val sharedPreference1 = activity!!.getSharedPreferences("Preference", Context.MODE_PRIVATE)
        if(sharedPreference1!=null) {
            userId = sharedPreference1.getString("Id", "")!!
        }else{
            activity!!.finish()
            Toast.makeText(activity, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        if(ConnectionManager().checkConnectivity(activity as Context)){
            try{
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
                val jsonObjectRequest = object: JsonObjectRequest(
                    Request.Method.GET,
                    url, null, Response.Listener {
                        try{
                            progressLayout.visibility = View.GONE
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if(success){
                                val data = data1.getJSONArray("data")
                                if(data.length()==0){
                                    Toast.makeText(
                                        activity,
                                        "No Orders Placed Yet!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    rlNoOrderHistory.visibility = View.VISIBLE
                                }else{
                                    rlNoOrderHistory.visibility = View.GONE
                                    for (i in 0 until data.length()) {
                                        val restaurantItemJsonObject = data.getJSONObject(i)

                                        val eachRestaurantObject = OrderHistoryRestaurant(
                                            restaurantItemJsonObject.getString("order_id"),
                                            restaurantItemJsonObject.getString("restaurant_name"),
                                            restaurantItemJsonObject.getString("total_cost"),
                                            restaurantItemJsonObject.getString("order_placed_at").substring(0,10)   )

                                        orderedRestaurantList.add(eachRestaurantObject)

                                        orderHistoryAdapter = OrderHistoryAdapter(activity as Context, orderedRestaurantList)
                                        recyclerOrderHistory.adapter = orderHistoryAdapter

                                        recyclerOrderHistory.layoutManager = layoutManager
                                        progressLayout.visibility = View.GONE
                                    }
                                }
                            }else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some Error Occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }catch (e: JSONException){
                            Toast.makeText(activity as Context,
                                "Some Unexpected Error Occurred",
                                Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                        if(activity!=null)
                            Toast.makeText(activity as Context,
                                "Volley Error occurred",
                                Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "919c7039d0ab7d"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            }catch (e: Exception){
                Toast.makeText(activity as Context,
                    "Some Unexpected Error Occurred",
                    Toast.LENGTH_SHORT).show()
            }
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

}
