package com.codiinggeek.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class WelcomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)
        Handler().postDelayed({
            val startAct = Intent(this@WelcomePage, Login::class.java)
            startActivity(startAct)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
