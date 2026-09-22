package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StreakActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var tvStreakTitle: TextView
    private lateinit var tvStreakSub: TextView
    private lateinit var btnCheckIn: Button

    private var currentStreak: Long = 0L
    private var lastActiveDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvStreakTitle = findViewById(R.id.tvStreakTitle)
        tvStreakSub = findViewById(R.id.tvStreakSub)
        btnCheckIn = findViewById(R.id.btnDailyCheckIn)

        btnCheckIn.setOnClickListener {
            performDailyCheckIn()
        }

        loadStreakData()
    }

    private fun loadStreakData() {
        val user = auth.currentUser
        if (user == null) {
            tvStreakTitle.text = "0 Day Streak"
            btnCheckIn.isEnabled = false
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                currentStreak = doc.getLong("streak") ?: 0L
                lastActiveDate = doc.getString("lastActiveDate") ?: ""

                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                tvStreakTitle.text = "🔥 $currentStreak Day Streak"

                if (lastActiveDate == today) {
                    btnCheckIn.text = "Checked In Today! ✅"
                    btnCheckIn.isEnabled = false
                    tvStreakSub.text = "Awesome work! You've already checked in for today."
                } else {
                    btnCheckIn.text = "Check In Today "
                    btnCheckIn.isEnabled = true
                    tvStreakSub.text = "Tap below to log your daily scripture check-in!"
                }
            }
            .addOnFailureListener { e ->
                Log.e("StreakActivity", "Failed to load streak", e)
            }
    }

    private fun performDailyCheckIn() {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val newStreak = currentStreak + 1

        userRef.get().addOnSuccessListener { doc ->
            val currentFp = doc.getLong("faithPoints") ?: 0L
            val newFp = currentFp + 10

            val updates = mapOf(
                "streak" to newStreak,
                "lastActiveDate" to today,
                "faithPoints" to newFp
            )

            userRef.update(updates)
                .addOnSuccessListener {
                    currentStreak = newStreak
                    lastActiveDate = today

                    tvStreakTitle.text = "🔥 $newStreak Day Streak"
                    btnCheckIn.text = "Checked In Today! ✅"
                    btnCheckIn.isEnabled = false
                    tvStreakSub.text = "Awesome work! You earned +10 Faith Points."

                    Toast.makeText(this, "Daily check-in successful! 🎉 (+10 FP)", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("StreakActivity", "Failed to check in", e)
                    Toast.makeText(this, "Failed to check in", Toast.LENGTH_SHORT).show()
                }
        }
    }
}