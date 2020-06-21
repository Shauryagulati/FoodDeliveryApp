package com.codiinggeek.food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class ForgotPassword : AppCompatActivity() {

    lateinit var eForgotMobile: EditText
    lateinit var eForgotEmail: EditText
    lateinit var btnNext: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        sharedPreferences = getSharedPreferences("Forgot", Context.MODE_PRIVATE)

        eForgotMobile = findViewById(R.id.eForgotMobile)
        eForgotEmail = findViewById(R.id.eForgotEmail)
        btnNext = findViewById(R.id.btnNext)

        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ForgotPassword)
        val jsonParams = JSONObject()

        btnNext.setOnClickListener {
            val mobile_number = eForgotMobile.text.toString()
            val email = eForgotEmail.text.toString()

            jsonParams.put("mobile_number", mobile_number)
            jsonParams.put("email", email)

            if(ConnectionManager().checkConnectivity(this@ForgotPassword)){
                val jsonRequest = object: JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        try{
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            intent = Intent(this@ForgotPassword, ForgotPassword2::class.java)
                            if(success){
                                savePreferences(mobile_number)
                                val first_try = data1.getBoolean("first_try")
                                if(first_try){
                                    Toast.makeText(this@ForgotPassword,
                                        "OTP sent to registered Email Id",
                                        Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this@ForgotPassword,
                                    "check for the previously sent OTP to the registered Email Id",
                                    Toast.LENGTH_SHORT).show()
                                }
                                startActivity(intent)
                            }else{
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "Some Error Occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }catch (e: Exception) {
                            Toast.makeText(
                                this@ForgotPassword,
                                "Some Error Occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPassword,
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
                val dialog = AlertDialog.Builder(this@ForgotPassword)
                dialog.setTitle("Failure")
                dialog.setMessage("No Internet Connection Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPassword)
                }
                dialog.create()
                dialog.show()
            }
        }

    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun savePreferences(number: String){
        sharedPreferences.edit().putString("Number", number).apply()
    }
}
