package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BookmarksActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var containerBookmarks: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val etReference = findViewById<EditText>(R.id.etBookmarkReference)
        val etText = findViewById<EditText>(R.id.etBookmarkText)
        val btnSave = findViewById<Button>(R.id.btnSaveBookmark)

        containerBookmarks = findViewById(R.id.containerBookmarks)
        progressBar = findViewById(R.id.progressBarBookmarks)
        tvEmpty = findViewById(R.id.tvEmptyBookmarks)

        btnSave.setOnClickListener {
            val ref = etReference.text.toString().trim()
            val text = etText.text.toString().trim()

            if (ref.isEmpty()) {
                etReference.error = "Enter reference"
                etReference.requestFocus()
                return@setOnClickListener
            }

            saveBookmark(ref, text) {
                etReference.setText("")
                etText.setText("")
                loadBookmarks()
            }
        }

        loadBookmarks()
    }

    private fun loadBookmarks() {
        val user = auth.currentUser
        if (user == null) {
            tvEmpty.visibility = View.VISIBLE
            tvEmpty.text = "Please log in to view bookmarks"
            return
        }

        progressBar.visibility = View.VISIBLE
        containerBookmarks.removeAllViews()

        db.collection("users")
            .document(user.uid)
            .collection("bookmarks")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshots ->
                progressBar.visibility = View.GONE

                if (snapshots.isEmpty) {
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.GONE
                    for (doc in snapshots) {
                        val id = doc.id
                        val ref = doc.getString("reference") ?: "Bookmark"
                        val text = doc.getString("text") ?: ""
                        addBookmarkCard(id, ref, text)
                    }
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("BookmarksActivity", "Failed to load bookmarks", e)
                Toast.makeText(this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addBookmarkCard(docId: String, reference: String, text: String) {
        val card = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 24)
            }
            radius = 32f
            cardElevation = 6f
            setCardBackgroundColor(getColor(R.color.HolyTextApplication_surface))
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val tvRef = TextView(this).apply {
            this.text = "🔖 $reference"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColor(R.color.HolyTextApplication_primary))
        }

        val tvBody = TextView(this).apply {
            this.text = text
            textSize = 15f
            setTextColor(getColor(R.color.HolyTextApplication_text_primary))
            setPadding(0, 12, 0, 16)
        }

        val btnDelete = Button(this).apply {
            this.text = "Delete"
            isAllCaps = false
            setBackgroundColor(getColor(R.color.HolyTextApplication_accent))
            setTextColor(getColor(R.color.HolyTextApplication_white))
            setOnClickListener {
                deleteBookmark(docId)
            }
        }

        layout.addView(tvRef)
        if (text.isNotEmpty()) {
            layout.addView(tvBody)
        }
        layout.addView(btnDelete)
        card.addView(layout)

        containerBookmarks.addView(card)
    }

    private fun saveBookmark(ref: String, text: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return

        val bookmark = mapOf(
            "reference" to ref,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(user.uid)
            .collection("bookmarks")
            .add(bookmark)
            .addOnSuccessListener {
                Toast.makeText(this, "Bookmark saved!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("BookmarksActivity", "Error saving", e)
                Toast.makeText(this, "Failed to save bookmark", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteBookmark(docId: String) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("bookmarks")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Bookmark deleted", Toast.LENGTH_SHORT).show()
                loadBookmarks()
            }
            .addOnFailureListener { e ->
                Log.e("BookmarksActivity", "Error deleting", e)
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }
}