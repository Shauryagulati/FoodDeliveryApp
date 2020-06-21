package com.codiinggeek.food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fragment.HomeFragment
import model.Detail
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class Login : AppCompatActivity() {
    lateinit var eMobileNumber: EditText
    lateinit var ePasword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotP: TextView
    lateinit var txtSignUp: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("Preference", Context.MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)
        if (isLoggedIn) {
            intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        title = "Log In"
        eMobileNumber = findViewById(R.id.eMobileNumber)
        ePasword = findViewById(R.id.ePassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotP = findViewById(R.id.txtForgotP)
        txtSignUp = findViewById(R.id.txtSignUp)

        val url = "http://13.235.250.119/v2/login/fetch_result"
        val queue = Volley.newRequestQueue(this@Login)
        val jsonParams = JSONObject()

        btnLogin.setOnClickListener {
            val mobile = eMobileNumber.text.toString()
            val pass = ePasword.text.toString()

            jsonParams.put("mobile_number", mobile)
            jsonParams.put("password", pass)

            if (ConnectionManager().checkConnectivity(this@Login)) {
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            intent = Intent(this@Login, MainActivity::class.java)
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
                                    this@Login,
                                    data1.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Login,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@Login,
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
                val dialog = AlertDialog.Builder(this@Login)
                dialog.setTitle("Failure")
                dialog.setMessage("No Internet Connection Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@Login)
                }
                dialog.create()
                dialog.show()
            }
        }
            txtForgotP.setOnClickListener {
                intent = Intent(this@Login, ForgotPassword::class.java)
                startActivity(intent)
            }
            txtSignUp.setOnClickListener {
                intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
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
