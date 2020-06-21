package com.codiinggeek.food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.Detail
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception
import java.sql.Connection


class Register : AppCompatActivity() {

    lateinit var toolbar : androidx.appcompat.widget.Toolbar

    lateinit var eName: EditText
    lateinit var eEmailAddress: EditText
    lateinit var eMobile: EditText
    lateinit var eDelivery: EditText
    lateinit var ePass: EditText
    lateinit var eCPass: EditText
    lateinit var btnReg: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.toolbar)
        setUpToolbar()

        eName = findViewById(R.id.eName)
        eEmailAddress = findViewById(R.id.eEmailAddress)
        eMobile = findViewById(R.id.eMobile)
        eDelivery = findViewById(R.id.eDelivery)
        ePass = findViewById(R.id.ePass)
        eCPass = findViewById(R.id.eCPass)
        btnReg = findViewById(R.id.btnReg)

        sharedPreferences = getSharedPreferences("Preference", Context.MODE_PRIVATE)

        val url = "http://13.235.250.119/v2/register/fetch_result"
        val queue = Volley.newRequestQueue(this@Register)
        val jsonParams = JSONObject()

            btnReg.setOnClickListener {
                val name = eName.text.toString()
                val mobile_number = eMobile.text.toString()
                val email = eEmailAddress.text.toString()
                val address = eDelivery.text.toString()
                val password = ePass.text.toString()
                val cPassword = eCPass.text.toString()

                if (password.compareTo(cPassword) != 0) {
                    Toast.makeText(
                        this@Register,
                        "Confirm Password did not match",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", mobile_number)
                    jsonParams.put("password", password)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)

                    if (ConnectionManager().checkConnectivity(this@Register)) {
                        val jsonRequest = object : JsonObjectRequest(
                            Request.Method.POST, url, jsonParams,
                            Response.Listener {
                                try {
                                    val data1 = it.getJSONObject("data")
                                    val success = data1.getBoolean("success")
                                    intent = Intent(this@Register, MainActivity::class.java)
                                    if (success) {
                                        val detailsObject = data1.getJSONObject("data")
                                        val details = Detail(
                                            detailsObject.getString("user_id"),
                                            detailsObject.getString("name"),
                                            detailsObject.getString("email"),
                                            detailsObject.getString("mobile_number"),
                                            detailsObject.getString("address")
                                        )
                                        savePreferences(details)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this@Register,
                                            "Unable to Register",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@Register,
                                        "Some Error Occurred!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@Register,
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
                    } else {
                        val dialog = AlertDialog.Builder(this@Register)
                        dialog.setTitle("Failure")
                        dialog.setMessage("No Internet Connection Found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@Register)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }

    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@Register, Login::class.java))
            finish()
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreferences(obj: Detail) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("Name", obj.name).apply()
        sharedPreferences.edit().putString("Mobile", obj.mobile).apply()
        sharedPreferences.edit().putString("Address", obj.address).apply()
        sharedPreferences.edit().putString("Email", obj.email).apply()
        sharedPreferences.edit().putString("Id", obj.id).apply()
    }
}
