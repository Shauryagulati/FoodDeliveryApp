package com.codiinggeek.food

import Adapter.RecyclerCartAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.net.ConnectivityManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.CartItems
import model.Detail
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class Cart : AppCompatActivity() {

    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var recyclerCart: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerCartAdapter: RecyclerCartAdapter

    lateinit var restaurantId:String
    lateinit var restaurantName:String
    lateinit var userId: String

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var selectedItemsId = arrayListOf<String>()
    var itemsName = arrayListOf<String>()
    var itemsPrice = arrayListOf<Int>()

    var totalAmount=0

    var cartListItems = arrayListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val sharedPreference1 = getSharedPreferences("Preference", Context.MODE_PRIVATE)
        if(sharedPreference1!=null) {
            userId = sharedPreference1.getString("Id", "")!!
        }else{
            finish()
            Toast.makeText(this@Cart, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        val sharedPreference2 = getSharedPreferences("Menu Data", Context.MODE_PRIVATE)
        if(sharedPreference2!=null) {
            restaurantId = sharedPreference2.getString("RestaurantId", "-1")!!
            restaurantName = sharedPreference2.getString("RestaurantName", "")!!
        }else{
            finish()
            Toast.makeText(this@Cart, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        if(restaurantId=="-1"){
            finish()
            Toast.makeText(this@Cart, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        if(intent==null){
            finish()
            Toast.makeText(this@Cart, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        selectedItemsId = intent.getStringArrayListExtra("selectedItemId")
        itemsPrice = intent.getIntegerArrayListExtra("itemPrice")
        itemsName = intent.getStringArrayListExtra("itemName")

        btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
        txtRestaurantName=findViewById(R.id.txtRestaurantName)
        recyclerCart = findViewById(R.id.recyclerCart)

        layoutManager = LinearLayoutManager(this@Cart)

        toolbar=findViewById(R.id.toolBar)
        setToolBar()

        txtRestaurantName.text = restaurantName
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        if (ConnectionManager().checkConnectivity(this)) {
            cartListItems.clear()
            for (i in 0 until itemsPrice.size) {
                totalAmount += itemsPrice[i]
                val item = CartItems(itemsName[i], itemsPrice[i].toString())
                cartListItems.add(item)
                recyclerCartAdapter = RecyclerCartAdapter(applicationContext, cartListItems)

                recyclerCart.adapter = recyclerCartAdapter
                recyclerCart.layoutManager = layoutManager
            }
            btnPlaceOrder.text = "Place Order(Total:Rs. $totalAmount)"
        }else{
            val dialog = AlertDialog.Builder(applicationContext)
            dialog.setTitle("Failure")
            dialog.setMessage("No Internet Connection Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                finish()
            }
            dialog.create()
            dialog.show()
        }


        btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {

                progressLayout.visibility = View.VISIBLE
                try {
                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                    val queue = Volley.newRequestQueue(this@Cart)
                    val jsonParams = JSONObject()

                    val foodJsonArray= JSONArray()
                    for (foodItem in selectedItemsId){
                        val singleItemObject=JSONObject()
                        singleItemObject.put("food_item_id",foodItem)
                        foodJsonArray.put(singleItemObject)
                    }

                    jsonParams.put("user_id",userId)
                    jsonParams.put("restaurant_id",restaurantId)
                    jsonParams.put("total_cost", totalAmount)
                    jsonParams.put("food",foodJsonArray)

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")

                                intent = Intent(this@Cart, OrderPlaced::class.java)
                                if (success) {
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val responseMessageServer =
                                        data1.getString("errorMessage")
                                    Toast.makeText(
                                        this,
                                        responseMessageServer.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this,
                                    "Some Error Occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            progressLayout.visibility = View.INVISIBLE
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this,
                                "Volley Error $it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "919c7039d0ab7d"
                            return headers
                        }
                    }
                    queue.add(jsonRequest)
                }catch (e: Exception) {
                    Toast.makeText(
                        this@Cart,
                        "Some Error Occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                val dialog = AlertDialog.Builder(applicationContext)
                dialog.setTitle("Failure")
                dialog.setMessage("No Internet Connection Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    finish()
                }
                dialog.create()
                dialog.show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){
            android.R.id.home->{
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
