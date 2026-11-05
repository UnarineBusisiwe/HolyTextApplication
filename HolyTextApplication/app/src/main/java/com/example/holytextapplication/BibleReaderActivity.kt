package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class BibleReaderActivity : AppCompatActivity() {

    private val api = BibleApiClient.service
    private var currentLanguage: String = "English"
    private var currentTextSizeSetting: String = "Medium"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bible_reader)

        // Load language and text size preferences
        val prefs = getSharedPreferences("holytext_prefs", MODE_PRIVATE)
        currentLanguage = prefs.getString("language", "English") ?: "English"
        currentTextSizeSetting = prefs.getString("textSize", "Medium") ?: "Medium"

        // Back arrow toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.subtitle = "Language: $currentLanguage"
        toolbar.setNavigationOnClickListener {
            Log.d("BibleReader", "Back arrow pressed")
            onBackPressedDispatcher.onBackPressed()
        }

        val spinnerBook = findViewById<Spinner>(R.id.spinnerBook)
        val etChapter = findViewById<EditText>(R.id.etChapter)
        val btnLoad = findViewById<Button>(R.id.btnLoadChapter)
        val tvReference = findViewById<TextView>(R.id.tvReference)
        val tvScripture = findViewById<TextView>(R.id.tvScripture)
        val progress = findViewById<ProgressBar>(R.id.progressBar)

        // Set font size based on settings
        val fontSizeSp = when (currentTextSizeSetting) {
            "Small" -> 14f
            "Large" -> 22f
            else -> 18f
        }
        tvScripture.textSize = fontSizeSp

        // Populate books of the Bible according to chosen language
        val books = BibleLanguageHelper.getBooksForLanguage(currentLanguage)
        spinnerBook.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            books
        )

        btnLoad.setOnClickListener {
            val selectedBookName = spinnerBook.selectedItem.toString()
            val chapterText = etChapter.text.toString().trim()

            // Validation
            if (chapterText.isEmpty()) {
                etChapter.error = "Enter a chapter number"
                etChapter.requestFocus()
                return@setOnClickListener
            }

            val chapterNumber = chapterText.toIntOrNull()
            if (chapterNumber == null || chapterNumber < 1 || chapterNumber > 150) {
                etChapter.error = "Enter a valid chapter number (1–150)"
                etChapter.requestFocus()
                return@setOnClickListener
            }

            // Convert localized book name (e.g., Genese / Mattheo) to English for API
            val englishBook = BibleLanguageHelper.toEnglishBookName(selectedBookName, currentLanguage)

            loadPassage(englishBook, chapterNumber, currentLanguage, tvReference, tvScripture, progress)
        }
    }

    /**
     * Awards +5 Faith Points to the current user in Firestore.
     */
    private fun awardFaithPoints() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val currentFp = doc.getLong("faithPoints") ?: 0L
                val newFp = currentFp + 5

                db.collection("users").document(user.uid)
                    .update("faithPoints", newFp)
                    .addOnSuccessListener {
                        Log.d("BibleReader", "Awarded 5 FP. New total: $newFp")
                    }
                    .addOnFailureListener { e ->
                        Log.e("BibleReader", "Failed to award FP", e)
                    }
            }
    }

    private fun loadPassage(
        englishBook: String,
        chapterNumber: Int,
        language: String,
        tvReference: TextView,
        tvScripture: TextView,
        progress: ProgressBar
    ) {
        progress.visibility = View.VISIBLE
        tvScripture.text = ""
        tvReference.text = ""

        val query = "${englishBook.replace(" ", "+")}+$chapterNumber"
        Log.d("BibleReader", "Loading query: $query in $language")

        lifecycleScope.launch {
            try {
                val response = api.getPassage(query, "kjv")
                progress.visibility = View.GONE

                if (response.verses.isNullOrEmpty()) {
                    Log.w("BibleReader", "No verses returned for $query")
                    Toast.makeText(
                        this@BibleReaderActivity,
                        "No results for that passage.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val localizedRef = BibleLanguageHelper.toLocalizedReference(englishBook, chapterNumber, language)
                tvReference.text = localizedRef

                // Translate verses to Sepedi / Tshivenda / English
                val localizedVerses = BibleLanguageHelper.translateVerses(
                    englishBook,
                    chapterNumber,
                    response.verses,
                    language
                )

                val formatted = localizedVerses.joinToString("\n\n") { v ->
                    "${v.verse ?: ""}. ${v.text?.trim() ?: ""}"
                }
                tvScripture.text = formatted

                Log.d("BibleReader", "Loaded ${response.verses.size} verses in $language")

                // Award points ONLY on success
                awardFaithPoints()

            } catch (e: Exception) {
                progress.visibility = View.GONE
                Log.e("BibleReader", "API call failed", e)
                Toast.makeText(
                    this@BibleReaderActivity,
                    "Could not load. Check internet connection.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}