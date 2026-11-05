package com.example.holytextapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val prefs by lazy {
        getSharedPreferences("holytext_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            Log.d("SettingsActivity", "Back arrow pressed")
            onBackPressedDispatcher.onBackPressed()
        }

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
        val spinnerTextSize = findViewById<Spinner>(R.id.spinnerTextSize)
        val switchNotifications = findViewById<SwitchMaterial>(R.id.switchNotifications)
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)
        val btnLogout = findViewById<Button>(R.id.btnLogoutSettings)


        // Language options
        val languages = arrayOf("English", "Tshivenda", "Sepedi")
        spinnerLanguage.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        // Text size options
        val textSizes = arrayOf("Small", "Medium", "Large")
        spinnerTextSize.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            textSizes
        )

        // Load saved preferences
        val savedLang = prefs.getString("language", "English") ?: "English"
        val savedSize = prefs.getString("textSize", "Medium") ?: "Medium"
        val savedNotif = prefs.getBoolean("notifications", true)

        spinnerLanguage.setSelection(languages.indexOf(savedLang).coerceAtLeast(0))
        spinnerTextSize.setSelection(textSizes.indexOf(savedSize).coerceAtLeast(0))
        switchNotifications.isChecked = savedNotif

        Log.d("SettingsActivity", "Loaded prefs: lang=$savedLang, size=$savedSize, notif=$savedNotif")

        // Save button
        btnSave.setOnClickListener {
            val language = spinnerLanguage.selectedItem.toString()
            val textSize = spinnerTextSize.selectedItem.toString()
            val notifications = switchNotifications.isChecked

            // 1. Save locally (SharedPreferences)
            prefs.edit()
                .putString("language", language)
                .putString("textSize", textSize)
                .putBoolean("notifications", notifications)
                .apply()

            Log.d("SettingsActivity", "Saved locally: $language / $textSize / $notifications")

            // 2. Save to Firestore (if logged in)
            val user = auth.currentUser
            if (user != null) {
                val updates = mapOf(
                    "preferredLanguage" to language,
                    "textSize" to textSize,
                    "notificationsEnabled" to notifications
                )
                db.collection("users").document(user.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        Log.d("SettingsActivity", "Firestore synced")
                        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SettingsActivity", "Firestore sync failed", e)
                        Toast.makeText(this, "Saved locally (sync pending)", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout button
        btnLogout.setOnClickListener {
            Log.d("SettingsActivity", "Logging out")
            auth.signOut()
            prefs.edit().clear().apply()   // optional: clear local prefs on logout
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}