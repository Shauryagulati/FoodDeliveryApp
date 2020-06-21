package com.codiinggeek.food

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class ForgotPassword2 : AppCompatActivity() {

    lateinit var eOTP : EditText
    lateinit var eNewPassword: EditText
    lateinit var eNewConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)

        eOTP = findViewById(R.id.eOTP)
        eNewPassword = findViewById(R.id.eNewPassword)
        eNewConfirmPassword = findViewById(R.id.eNewConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        sharedPreferences = getSharedPreferences("Forgot", Context.MODE_PRIVATE)

        val number = sharedPreferences.getString("Number", "")

        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ForgotPassword2)
        val jsonParams = JSONObject()

        btnSubmit.setOnClickListener {
            val mobile_number = number
            val password = eNewPassword.text.toString()
            val otp = eOTP.text.toString()
            val nPassword = eNewConfirmPassword.text.toString()

            if(password.compareTo(nPassword)!=0){
                Toast.makeText(
                    this@ForgotPassword2,
                    "Confirm Password did not match",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                jsonParams.put("mobile_number", mobile_number)
                jsonParams.put("password", password)
                jsonParams.put("otp", otp)

                if(ConnectionManager().checkConnectivity(this@ForgotPassword2)){
                    val jsonRequest = object: JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                intent = Intent(this@ForgotPassword2, Login::class.java)
                                if(success) {
                                    val successMessage = data1.getString("successMessage")
                                    Toast.makeText(
                                        this@ForgotPassword2,
                                        successMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sharedPreferences.edit().clear().apply()
                                    startActivity(intent)
                                }else {
                                        Toast.makeText(
                                            this@ForgotPassword2,
                                            "Some Error Occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }

                            }catch (e: Exception) {
                                Toast.makeText(
                                    this@ForgotPassword2,
                                    "Some Error Occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPassword2,
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
                    val dialog = AlertDialog.Builder(this@ForgotPassword2)
                    dialog.setTitle("Failure")
                    dialog.setMessage("No Internet Connection Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotPassword2)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}