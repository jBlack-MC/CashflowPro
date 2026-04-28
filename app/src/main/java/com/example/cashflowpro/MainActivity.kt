package com.example.cashflowpro

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAnimations()

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setupAnimations() {
        val dot1 = findViewById<View>(R.id.dot1)
        val dot2 = findViewById<View>(R.id.dot2)
        val dot3 = findViewById<View>(R.id.dot3)

        val pulseAnimation = ValueAnimator.ofFloat(0.6f, 1.0f, 0.6f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                dot1.scaleX = scale
                dot1.scaleY = scale
                dot1.alpha = if (scale > 0.7f) 1.0f else 0.6f
            }
            startDelay = 0
        }

        val pulseAnimation2 = ValueAnimator.ofFloat(0.6f, 1.0f, 0.6f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            startDelay = 160
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                dot2.scaleX = scale
                dot2.scaleY = scale
                dot2.alpha = if (scale > 0.7f) 1.0f else 0.6f
            }
        }

        val pulseAnimation3 = ValueAnimator.ofFloat(0.6f, 1.0f, 0.6f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            startDelay = 320
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                dot3.scaleX = scale
                dot3.scaleY = scale
                dot3.alpha = if (scale > 0.7f) 1.0f else 0.6f
            }
        }

        pulseAnimation.start()
        pulseAnimation2.start()
        pulseAnimation3.start()
    }
}
