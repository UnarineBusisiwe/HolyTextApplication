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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var containerNotes: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val etTitle = findViewById<EditText>(R.id.etNoteTitle)
        val etContent = findViewById<EditText>(R.id.etNoteContent)
        val btnSave = findViewById<Button>(R.id.btnSaveNote)

        containerNotes = findViewById(R.id.containerNotes)
        progressBar = findViewById(R.id.progressBarNotes)
        tvEmpty = findViewById(R.id.tvEmptyNotes)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Enter note title"
                etTitle.requestFocus()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                etContent.error = "Enter note content"
                etContent.requestFocus()
                return@setOnClickListener
            }

            saveNote(title, content) {
                etTitle.setText("")
                etContent.setText("")
                loadNotes()
            }
        }

        loadNotes()
    }

    private fun loadNotes() {
        val user = auth.currentUser
        if (user == null) {
            tvEmpty.visibility = View.VISIBLE
            tvEmpty.text = "Please sign in to view notes"
            return
        }

        progressBar.visibility = View.VISIBLE
        containerNotes.removeAllViews()

        db.collection("users")
            .document(user.uid)
            .collection("notes")
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
                        val title = doc.getString("title") ?: "Untitled Note"
                        val content = doc.getString("content") ?: ""
                        val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        addNoteCard(id, title, content, ts)
                    }
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("NotesActivity", "Failed to load notes", e)
                Toast.makeText(this, "Failed to load notes", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addNoteCard(docId: String, title: String, content: String, timestamp: Long) {
        val formattedDate = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
            .format(Date(timestamp))

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

        val tvTitle = TextView(this).apply {
            text = title
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColor(R.color.HolyTextApplication_primary))
        }

        val tvDate = TextView(this).apply {
            text = formattedDate
            textSize = 12f
            setTextColor(getColor(R.color.HolyTextApplication_text_secondary))
            setPadding(0, 4, 0, 12)
        }

        val tvContent = TextView(this).apply {
            text = content
            textSize = 15f
            setTextColor(getColor(R.color.HolyTextApplication_text_primary))
            setPadding(0, 0, 0, 16)
        }

        val btnDelete = Button(this).apply {
            text = "Delete Note"
            isAllCaps = false
            setBackgroundColor(getColor(R.color.HolyTextApplication_accent))
            setTextColor(getColor(R.color.HolyTextApplication_white))
            setOnClickListener {
                deleteNote(docId)
            }
        }

        layout.addView(tvTitle)
        layout.addView(tvDate)
        layout.addView(tvContent)
        layout.addView(btnDelete)
        card.addView(layout)

        containerNotes.addView(card)
    }

    private fun saveNote(title: String, content: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return

        val note = mapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )

        val userRef = db.collection("users").document(user.uid)

        userRef.collection("notes")
            .add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note saved! (+5 Faith Points)", Toast.LENGTH_SHORT).show()
                awardFaithPoints(5)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("NotesActivity", "Error saving note", e)
                Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNote(docId: String) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("notes")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
            .addOnFailureListener { e ->
                Log.e("NotesActivity", "Error deleting note", e)
                Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun awardFaithPoints(points: Long) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentFp = doc.getLong("faithPoints") ?: 0L
            val newFp = currentFp + points
            userRef.update("faithPoints", newFp)
        }
    }
}