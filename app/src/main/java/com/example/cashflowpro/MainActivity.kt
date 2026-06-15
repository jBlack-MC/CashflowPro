package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            
            val rootLayout = findViewById<View>(R.id.main)
            if (rootLayout != null) {
                ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }

            setupEntranceAnimations()

            findViewById<View>(R.id.dashboardPreview)?.setOnClickListener {
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                finish()
            }

            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGetStarted)?.setOnClickListener {
                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            }
            
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSignIn)?.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Crash in MainActivity", e)
        }
    }

    private fun setupEntranceAnimations() {
        val logoCard = findViewById<View>(R.id.mainLogoCard)
        val appName = findViewById<View>(R.id.tvAppName)
        val appSubtitle = findViewById<View>(R.id.tvAppSubtitle)
        val dashboardPreview = findViewById<View>(R.id.dashboardPreview)
        val featuresList = findViewById<View>(R.id.featuresList)
        val actionArea = findViewById<View>(R.id.actionArea)

        // Initial states
        logoCard?.let {
            it.alpha = 0f
            it.scaleX = 0.8f
            it.scaleY = 0.8f
        }
        
        appName?.let {
            it.alpha = 0f
            it.translationY = 20f
        }
        
        appSubtitle?.let {
            it.alpha = 0f
            it.translationY = 20f
        }
        
        dashboardPreview?.let {
            it.alpha = 0f
            it.translationY = 100f
        }
        
        featuresList?.let {
            it.alpha = 0f
            it.translationY = 50f
        }
        
        actionArea?.let {
            it.alpha = 0f
        }

        // Animation Sequence
        logoCard?.animate()
            ?.alpha(1f)
            ?.scaleX(1f)
            ?.scaleY(1f)
            ?.setDuration(800)
            ?.setInterpolator(OvershootInterpolator())
            ?.setStartDelay(200)
            ?.start()

        appName?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(600)
            ?.setStartDelay(500)
            ?.start()

        appSubtitle?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(600)
            ?.setStartDelay(650)
            ?.start()

        dashboardPreview?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(1000)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.setStartDelay(800)
            ?.start()

        featuresList?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(800)
            ?.setStartDelay(1100)
            ?.start()

        actionArea?.animate()
            ?.alpha(1f)
            ?.setDuration(1000)
            ?.setStartDelay(1400)
            ?.start()
    }
}
