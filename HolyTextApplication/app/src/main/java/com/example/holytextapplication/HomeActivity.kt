package com.example.holytextapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // ✅ Class-level so helper methods can update them
    private lateinit var tvWelcome: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvFaithPoints: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbar back arrow
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            Log.d("HomeActivity", "Back arrow pressed")
            onBackPressedDispatcher.onBackPressed()
        }

        // Wire views
        tvWelcome = findViewById(R.id.tvWelcome)
        tvStreak = findViewById(R.id.tvStreak)
        tvFaithPoints = findViewById(R.id.tvFaithPoints)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnReadBible = findViewById<Button>(R.id.btnReadBible)
        val btnTrivia = findViewById<Button>(R.id.btnTrivia)

        // Navigation buttons
        btnSettings.setOnClickListener {
            Log.d("HomeActivity", "Open Settings")
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        btnReadBible.setOnClickListener {
            Log.d("HomeActivity", "Open Bible Reader")
            startActivity(Intent(this, BibleReaderActivity::class.java))
        }
        btnTrivia.setOnClickListener {
            Log.d("HomeActivity", "Open Bible Trivia")
            startActivity(Intent(this, TriviaActivity::class.java))
        }
        btnLogout.setOnClickListener {
            Log.d("HomeActivity", "Logout clicked")
            auth.signOut()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Must be logged in
        val user = auth.currentUser
        if (user == null) {
            Log.w("HomeActivity", "No logged-in user – redirecting to Main")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        Log.d("HomeActivity", "Home loaded for ${user.email}")

        // Load profile from Firestore
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Friend"
                val streak = doc.getLong("streak") ?: 0L
                val fp = doc.getLong("faithPoints") ?: 0L
                val preferredLang = doc.getString("preferredLanguage") ?: "English"
                val textSize = doc.getString("textSize") ?: "Medium"
                val notifications = doc.getBoolean("notificationsEnabled") ?: true

                // Sync Firestore preferences to local SharedPreferences
                val prefs = getSharedPreferences("holytext_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("language", preferredLang)
                    .putString("textSize", textSize)
                    .putBoolean("notifications", notifications)
                    .apply()

                Log.d("HomeActivity", "Loaded profile: name=$name, streak=$streak, fp=$fp, lang=$preferredLang")

                tvWelcome.text = "Welcome, $name"
                tvStreak.text = "🔥 $streak day streak"
                tvFaithPoints.text = "⭐ $fp Faith Points"

                // After the profile loads, check if streak needs updating
                updateStreakIfNewDay()
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Failed to load profile", e)
                tvWelcome.text = "Welcome"
            }
    }

    /**
     * Increments the streak if the user hasn't opened the app today.
     */
    private fun updateStreakIfNewDay() {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val lastActive = doc.getString("lastActiveDate") ?: ""
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())

            if (lastActive != today) {
                val currentStreak = doc.getLong("streak") ?: 0L
                val newStreak = currentStreak + 1

                userRef.update(
                    "streak", newStreak,
                    "lastActiveDate", today
                ).addOnSuccessListener {
                    Log.d("HomeActivity", "Streak updated: $currentStreak -> $newStreak")
                    tvStreak.text = "🔥 $newStreak day streak"
                }.addOnFailureListener { e ->
                    Log.e("HomeActivity", "Failed to update streak", e)
                }
            } else {
                Log.d("HomeActivity", "Streak already counted today")
            }
        }
    }
}