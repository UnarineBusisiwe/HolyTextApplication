package com.example.holytextapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase FIRST
        FirebaseApp.initializeApp(this)
        Log.d("MainActivity", "Firebase initialised: ${FirebaseApp.getApps(this).isNotEmpty()}")

        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("MainActivity", "Current user: ${currentUser?.email ?: "none"}")

        // If already logged in, skip straight to Home
        if (currentUser != null) {
            Log.d("MainActivity", "User already logged in – redirecting to Home")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val startReadingButton = findViewById<Button>(R.id.btnStartReading)
        startReadingButton.setOnClickListener {
            Log.d("MainActivity", "Start Reading clicked")
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val btnReadBible = findViewById<Button>(R.id.btnReadBible)
        btnReadBible.setOnClickListener {
            Log.d("MainActivity", "Read Bible clicked")
            startActivity(Intent(this, BibleReaderActivity::class.java))
        }
    }
}
