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
import kotlinx.coroutines.launch

class BibleReaderActivity : AppCompatActivity() {

    private val api = BibleApiClient.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bible_reader)

        // Back arrow toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
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

        // Populate books of the Bible
        val books = arrayOf(
            "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy",
            "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel",
            "1 Kings", "2 Kings", "Psalms", "Proverbs", "Ecclesiastes",
            "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel",
            "Matthew", "Mark", "Luke", "John", "Acts", "Romans",
            "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians",
            "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians",
            "1 Timothy", "2 Timothy", "Titus", "Hebrews", "James",
            "1 Peter", "2 Peter", "1 John", "2 John", "3 John",
            "Jude", "Revelation"
        )
        spinnerBook.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            books
        )

        btnLoad.setOnClickListener {
            val book = spinnerBook.selectedItem.toString()
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

            // Encode the query with + instead of a space
            val encodedQuery = "${book.replace(" ", "+")}+$chapterNumber"
            loadPassage(encodedQuery, tvReference, tvScripture, progress)
        }
    }

    /**
     * Awards +5 Faith Points to the current user in Firestore.
     */
    private fun awardFaithPoints() {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser ?: return
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

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
        query: String,
        tvReference: TextView,
        tvScripture: TextView,
        progress: ProgressBar
    ) {
        progress.visibility = View.VISIBLE
        tvScripture.text = ""
        tvReference.text = ""

        Log.d("BibleReader", "Loading query: $query")

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

                tvReference.text = response.reference ?: query

                val formatted = response.verses.joinToString("\n\n") { v ->
                    "${v.verse ?: ""}. ${v.text?.trim() ?: ""}"
                }
                tvScripture.text = formatted

                Log.d("BibleReader", "Loaded ${response.verses.size} verses")

                //  Award points ONLY on success
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