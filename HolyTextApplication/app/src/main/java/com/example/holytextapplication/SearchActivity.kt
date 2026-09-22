package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private val api = BibleApiClient.service
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private var currentReference: String = ""
    private var currentText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val etSearchQuery = findViewById<EditText>(R.id.etSearchQuery)
        val btnSearch = findViewById<Button>(R.id.btnSearchScripture)
        val btnSuggest1 = findViewById<Button>(R.id.btnSuggest1)
        val btnSuggest2 = findViewById<Button>(R.id.btnSuggest2)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarSearch)
        val cardResult = findViewById<CardView>(R.id.cardSearchResult)
        val tvReference = findViewById<TextView>(R.id.tvSearchReference)
        val tvContent = findViewById<TextView>(R.id.tvSearchContent)
        val btnBookmark = findViewById<Button>(R.id.btnBookmarkResult)

        btnSearch.setOnClickListener {
            val query = etSearchQuery.text.toString().trim()
            if (query.isEmpty()) {
                etSearchQuery.error = "Enter a verse or passage (e.g. John 3:16)"
                etSearchQuery.requestFocus()
                return@setOnClickListener
            }
            performSearch(query, progressBar, cardResult, tvReference, tvContent)
        }

        btnSuggest1.setOnClickListener {
            etSearchQuery.setText("John 3:16")
            performSearch("John 3:16", progressBar, cardResult, tvReference, tvContent)
        }

        btnSuggest2.setOnClickListener {
            etSearchQuery.setText("Psalm 23")
            performSearch("Psalm 23", progressBar, cardResult, tvReference, tvContent)
        }

        btnBookmark.setOnClickListener {
            saveBookmark(currentReference, currentText)
        }
    }

    private fun performSearch(
        query: String,
        progressBar: ProgressBar,
        cardResult: CardView,
        tvReference: TextView,
        tvContent: TextView
    ) {
        progressBar.visibility = View.VISIBLE
        cardResult.visibility = View.GONE

        val encodedQuery = query.replace(" ", "+")
        Log.d("SearchActivity", "Searching for passage: $encodedQuery")

        lifecycleScope.launch {
            try {
                val response = api.getPassage(encodedQuery, "kjv")
                progressBar.visibility = View.GONE

                if (response.verses.isNullOrEmpty()) {
                    Toast.makeText(
                        this@SearchActivity,
                        "No verses found for '$query'. Try e.g. John 3:16",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                currentReference = response.reference ?: query
                val formattedVerses = response.verses.joinToString("\n\n") { v ->
                    "${v.verse ?: ""}. ${v.text?.trim() ?: ""}"
                }
                currentText = formattedVerses

                tvReference.text = currentReference
                tvContent.text = currentText
                cardResult.visibility = View.VISIBLE

                awardFaithPoints(5)

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("SearchActivity", "Search failed", e)
                Toast.makeText(
                    this@SearchActivity,
                    "Failed to load verse. Check internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveBookmark(reference: String, text: String) {
        val user = auth.currentUser
        if (user == null || reference.isEmpty()) {
            Toast.makeText(this, "Cannot bookmark: Not signed in", Toast.LENGTH_SHORT).show()
            return
        }

        val bookmark = mapOf(
            "reference" to reference,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(user.uid)
            .collection("bookmarks")
            .add(bookmark)
            .addOnSuccessListener {
                Toast.makeText(this, "Bookmark saved successfully! 🔖", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("SearchActivity", "Failed to save bookmark", e)
                Toast.makeText(this, "Failed to save bookmark", Toast.LENGTH_SHORT).show()
            }
    }

    private fun awardFaithPoints(points: Long) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentFp = doc.getLong("faithPoints") ?: 0L
            val newFp = currentFp + points
            userRef.update("faithPoints", newFp)
                .addOnSuccessListener {
                    Log.d("SearchActivity", "Awarded $points FP. Total: $newFp")
                }
        }
    }
}