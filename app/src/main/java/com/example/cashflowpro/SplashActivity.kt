package com.example.cashflowpro

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_splash)

            val logoContainer = findViewById<LinearLayout>(R.id.logoContainer)
            val logoCard = findViewById<MaterialCardView>(R.id.logoCard)

            logoContainer?.apply {
                alpha = 0f
                translationY = 120f
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .start()
            }

            logoCard?.apply {
                scaleX = 0.8f
                scaleY = 0.8f
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(800)
                    .start()

                val floating = ObjectAnimator.ofFloat(
                    this,
                    "translationY",
                    0f,
                    -10f,
                    0f
                )
                floating.duration = 3500
                floating.repeatCount = ValueAnimator.INFINITE
                floating.start()
            }

            // Standard splash screen delay
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    android.util.Log.e("Splash", "Navigation failed", e)
                }
            }, 3000)

            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGetStarted)?.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("Splash", "Crash in SplashActivity", e)
            // Fallback: Try to start MainActivity immediately
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
