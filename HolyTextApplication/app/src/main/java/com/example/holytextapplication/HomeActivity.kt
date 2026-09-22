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

    private lateinit var tvWelcome: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvFaithPoints: TextView

    private lateinit var btnReadBible: Button
    private lateinit var btnSearch: Button
    private lateinit var btnBookmarks: Button
    private lateinit var btnNotes: Button
    private lateinit var btnReadingPlans: Button
    private lateinit var btnAudioBible: Button
    private lateinit var btnTrivia: Button
    private lateinit var btnWordSearch: Button
    private lateinit var btnStreak: Button
    private lateinit var btnAchievements: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button

    private var userName: String = "Friend"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(
            R.id.toolbar
        )

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Header
        tvWelcome = findViewById(R.id.tvWelcome)
        tvStreak = findViewById(R.id.tvStreak)
        tvFaithPoints = findViewById(R.id.tvFaithPoints)

        // Buttons
        btnReadBible = findViewById(R.id.btnReadBible)
        btnSearch = findViewById(R.id.btnSearch)
        btnBookmarks = findViewById(R.id.btnBookmarks)
        btnNotes = findViewById(R.id.btnNotes)
        btnReadingPlans = findViewById(R.id.btnReadingPlans)
        btnAudioBible = findViewById(R.id.btnAudioBible)
        btnTrivia = findViewById(R.id.btnTrivia)
        btnWordSearch = findViewById(R.id.btnWordSearch)
        btnStreak = findViewById(R.id.btnStreak)
        btnAchievements = findViewById(R.id.btnAchievements)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)

        // Listeners
        btnReadBible.setOnClickListener {
            startActivity(Intent(this, BibleReaderActivity::class.java))
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        btnBookmarks.setOnClickListener {
            startActivity(Intent(this, BookmarksActivity::class.java))
        }

        btnNotes.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }

        btnReadingPlans.setOnClickListener {
            startActivity(Intent(this, ReadingPlansActivity::class.java))
        }

        btnAudioBible.setOnClickListener {
            startActivity(Intent(this, AudioBibleActivity::class.java))
        }

        btnTrivia.setOnClickListener {
            startActivity(Intent(this, TriviaActivity::class.java))
        }

        btnWordSearch.setOnClickListener {
            startActivity(Intent(this, WordSearchActivity::class.java))
        }

        btnStreak.setOnClickListener {
            startActivity(Intent(this, StreakActivity::class.java))
        }

        btnAchievements.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        loadUserProfile(user.uid)
    }

    override fun onResume() {
        super.onResume()
        applyLanguageTranslations()
        auth.currentUser?.uid?.let { loadUserProfile(it) }
    }

    private fun applyLanguageTranslations() {
        val lang = LanguageManager.getSelectedLanguage(this)

        tvWelcome.text = LanguageManager.getWelcomeText(lang, userName)
        btnReadBible.text = LanguageManager.getReadBibleText(lang)
        btnSearch.text = LanguageManager.getSearchBibleText(lang)
        btnBookmarks.text = LanguageManager.getBookmarksText(lang)
        btnNotes.text = LanguageManager.getNotesText(lang)
        btnReadingPlans.text = LanguageManager.getReadingPlansText(lang)
        btnAudioBible.text = LanguageManager.getAudioBibleText(lang)
        btnTrivia.text = LanguageManager.getTriviaText(lang)
        btnWordSearch.text = LanguageManager.getWordSearchText(lang)
        btnStreak.text = LanguageManager.getStreakText(lang)
        btnAchievements.text = LanguageManager.getAchievementsText(lang)
        btnSettings.text = LanguageManager.getSettingsBtnText(lang)
        btnLogout.text = LanguageManager.getLogoutText(lang)
    }

    private fun loadUserProfile(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                userName = doc.getString("name") ?: "Friend"
                val streak = doc.getLong("streak") ?: 0L
                val faithPoints = doc.getLong("faithPoints") ?: 0L

                val lang = LanguageManager.getSelectedLanguage(this)
                tvWelcome.text = LanguageManager.getWelcomeText(lang, userName)
                tvStreak.text = "🔥 $streak day streak"
                tvFaithPoints.text = "⭐ $faithPoints Faith Points"

                updateStreakIfNewDay()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Failed to load profile", exception)
                val lang = LanguageManager.getSelectedLanguage(this)
                tvWelcome.text = LanguageManager.getWelcomeText(lang, "Friend")
            }
    }

    private fun updateStreakIfNewDay() {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val lastActive = doc.getString("lastActiveDate") ?: ""
            val today = java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
            ).format(java.util.Date())

            if (lastActive != today) {
                val currentStreak = doc.getLong("streak") ?: 0L
                val newStreak = currentStreak + 1

                userRef.set(
                    mapOf("streak" to newStreak, "lastActiveDate" to today),
                    com.google.firebase.firestore.SetOptions.merge()
                ).addOnSuccessListener {
                    tvStreak.text = "🔥 $newStreak day streak"
                }
            }
        }
    }
}