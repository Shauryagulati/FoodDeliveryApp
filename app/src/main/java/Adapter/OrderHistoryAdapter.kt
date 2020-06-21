package Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codiinggeek.food.R
import model.CartItems
import model.OrderHistoryRestaurant
import org.json.JSONException
import util.ConnectionManager

class OrderHistoryAdapter(val context: Context, val orderedRestaurantList:ArrayList<OrderHistoryRestaurant>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>()  {
    class ViewHolderOrderHistoryRestaurant(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtRestName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerOrderDetails: RecyclerView = view.findViewById(R.id.recyclerOrderDetails)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistoryRestaurant {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_order_history, parent, false)

        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {
        val restaurantObject = orderedRestaurantList[position]

        holder.txtRestName.text = restaurantObject.restaurantName
        var formatDate=restaurantObject.orderPlacedAt
        formatDate=formatDate.replace("-","/")
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)
        holder.txtDate.text =  formatDate

        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: RecyclerCartAdapter

        if(ConnectionManager().checkConnectivity(context)){
            try{
                var userId: String? = ""
                val orderItemsPerRestaurant=ArrayList<CartItems>()

                val sharedPreference1 = context.getSharedPreferences("Preference", Context.MODE_PRIVATE)
                if(sharedPreference1!=null) {
                    userId = sharedPreference1.getString("Id", "")!!
                }else{
                    (context as Activity).finish()
                    Toast.makeText(context, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
                }

                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
                val jsonObjectRequest = object: JsonObjectRequest(
                    Request.Method.GET,
                    url, null, Response.Listener {
                        try{
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if(success){
                                val data = data1.getJSONArray("data")

                                val fetchedRestaurantJsonObject = data.getJSONObject(position)//restaurant at index of position

                                orderItemsPerRestaurant.clear()

                                val foodOrderedJsonArray= fetchedRestaurantJsonObject.getJSONArray("food_items")

                                for(j in 0 until foodOrderedJsonArray.length())//loop through all the items
                                {
                                    val eachFoodItem = foodOrderedJsonArray.getJSONObject(j)//each food item
                                    val itemObject = CartItems(
                                        eachFoodItem.getString("name"),
                                        eachFoodItem.getString("cost")
                                    )
                                    orderItemsPerRestaurant.add(itemObject)
                                }

                                orderedItemAdapter = RecyclerCartAdapter(
                                    context,
                                    orderItemsPerRestaurant
                                )

                                holder.recyclerOrderDetails.adapter = orderedItemAdapter

                                holder.recyclerOrderDetails.layoutManager = layoutManager
                            }else {
                                Toast.makeText(
                                    context,
                                    "Some Error Occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }catch (e: JSONException){
                            Toast.makeText(context,
                                "Some Unexpected Error Occurred",
                                Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                            Toast.makeText(context,
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
                Toast.makeText(context,
                    "Some Unexpected Error Occurred",
                    Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context,
                "Some Unexpected Error Occurred",
                Toast.LENGTH_SHORT).show()
        }
    }

}