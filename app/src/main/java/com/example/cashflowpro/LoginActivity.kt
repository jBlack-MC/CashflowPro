package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: LoginActivity started")

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBiometric = findViewById<MaterialButton>(R.id.btnBiometric)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        val db = AppDatabase.getDatabase(this)

        setupBiometric()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.password)) {
                    Log.d("Auth", "Login Successful: $email")
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Log.d("Auth", "Login Failed: Invalid credentials for $email")
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBiometric.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        tvCreateAccount.setOnClickListener {
            Log.d(TAG, "registerBtn clicked: Navigating to RegisterActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Auth error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("Auth", "Biometric Login Successful")
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Auth failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Cashflow Pro")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }
}
