package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: LoginActivity started")

        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val registerBtn = findViewById<TextView>(R.id.tvCreateAccount)

        loginBtn.setOnClickListener {
            Log.d(TAG, "loginBtn clicked: Navigating to Dashboard")
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        registerBtn.setOnClickListener {
            Log.d(TAG, "registerBtn clicked: Navigating to RegisterActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
