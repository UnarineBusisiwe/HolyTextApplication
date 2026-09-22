package com.example.holytextapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val prefs by lazy {
        getSharedPreferences("holytext_prefs", Context.MODE_PRIVATE)
    }

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvLanguageLabel: TextView
    private lateinit var tvTextSizeLabel: TextView
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
        val spinnerTextSize = findViewById<Spinner>(R.id.spinnerTextSize)
        tvLanguageLabel = findViewById(R.id.tvLanguageLabel)
        tvTextSizeLabel = findViewById(R.id.tvTextSizeLabel)
        switchNotifications = findViewById(R.id.switchNotifications)
        btnSave = findViewById(R.id.btnSaveSettings)
        btnLogout = findViewById(R.id.btnLogoutSettings)

        // Languages array
        val languages = arrayOf("English", "Tshivenda", "Sepedi")
        spinnerLanguage.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        // Text sizes array
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

        updateUIForLanguage(savedLang)

        // On Spinner language selected: live update UI text
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLang = languages[position]
                updateUIForLanguage(selectedLang)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSave.setOnClickListener {
            val language = spinnerLanguage.selectedItem.toString()
            val textSize = spinnerTextSize.selectedItem.toString()
            val notifications = switchNotifications.isChecked

            prefs.edit()
                .putString("language", language)
                .putString("textSize", textSize)
                .putBoolean("notifications", notifications)
                .apply()

            val user = auth.currentUser
            if (user != null) {
                val updates = mapOf(
                    "preferredLanguage" to language,
                    "textSize" to textSize,
                    "notificationsEnabled" to notifications
                )
                db.collection("users").document(user.uid)
                    .set(updates, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Settings saved! (${language})", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Saved locally", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            prefs.edit().clear().apply()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateUIForLanguage(lang: String) {
        toolbar.title = LanguageManager.getSettingsTitle(lang)
        tvLanguageLabel.text = LanguageManager.getLanguageLabel(lang)
        tvTextSizeLabel.text = LanguageManager.getTextSizeLabel(lang)
        switchNotifications.text = LanguageManager.getNotificationsLabel(lang)
        btnSave.text = LanguageManager.getSaveSettingsText(lang)
        btnLogout.text = LanguageManager.getLogoutText(lang)
    }
}