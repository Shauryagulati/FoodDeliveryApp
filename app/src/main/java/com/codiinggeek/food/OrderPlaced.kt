package com.codiinggeek.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout

class OrderPlaced : AppCompatActivity() {

    lateinit var buttonOkay: Button
    lateinit var orderSuccessfullyPlaced: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        orderSuccessfullyPlaced=findViewById(R.id.orderSuccessfullyPlaced)
        buttonOkay=findViewById(R.id.buttonOkay)

        buttonOkay.setOnClickListener(View.OnClickListener {

            val intent= Intent(this, MainActivity::class.java)

            startActivity(intent)

            finishAffinity()
        })
    }
}
