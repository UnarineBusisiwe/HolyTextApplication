package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WordSearchActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val targetWords = listOf("FAITH", "GRACE", "JESUS", "PRAY", "HOPE")
    private val foundWords = mutableSetOf<String>()

    private val gridMatrix = arrayOf(
        charArrayOf('F', 'A', 'I', 'T', 'H', 'X', 'B'),
        charArrayOf('G', 'R', 'A', 'C', 'E', 'Y', 'I'),
        charArrayOf('J', 'E', 'S', 'U', 'S', 'Z', 'B'),
        charArrayOf('P', 'R', 'A', 'Y', 'W', 'Q', 'L'),
        charArrayOf('H', 'O', 'P', 'E', 'M', 'N', 'E'),
        charArrayOf('B', 'I', 'B', 'L', 'E', 'K', 'V'),
        charArrayOf('P', 'R', 'A', 'I', 'S', 'E', 'S')
    )

    private lateinit var tvProgress: TextView
    private lateinit var tvTargetList: TextView
    private lateinit var gridContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvProgress = findViewById(R.id.tvWordSearchProgress)
        tvTargetList = findViewById(R.id.tvTargetWords)
        gridContainer = findViewById(R.id.gridContainer)

        val etGuess = findViewById<EditText>(R.id.etWordGuess)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitWordGuess)

        renderGrid()
        updateUI()

        btnSubmit.setOnClickListener {
            val guess = etGuess.text.toString().trim().uppercase()
            if (guess.isEmpty()) {
                etGuess.error = "Enter a word"
                etGuess.requestFocus()
                return@setOnClickListener
            }

            checkWordGuess(guess)
            etGuess.setText("")
        }
    }

    private fun renderGrid() {
        gridContainer.removeAllViews()

        for (row in gridMatrix) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 4, 0, 4)
                }
            }

            for (char in row) {
                val tile = TextView(this).apply {
                    text = char.toString()
                    textSize = 20f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(getColor(R.color.HolyTextApplication_primary))
                    setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                    width = 100
                    height = 100
                    gravity = android.view.Gravity.CENTER
                }
                rowLayout.addView(tile)
            }
            gridContainer.addView(rowLayout)
        }
    }

    private fun checkWordGuess(word: String) {
        if (targetWords.contains(word)) {
            if (foundWords.contains(word)) {
                Toast.makeText(this, "You already found '$word'!", Toast.LENGTH_SHORT).show()
            } else {
                foundWords.add(word)
                Toast.makeText(this, "🎉 Found word: $word!", Toast.LENGTH_SHORT).show()
                updateUI()

                if (foundWords.size == targetWords.size) {
                    Toast.makeText(this, "Puzzle Complete! +15 Faith Points! 🏆", Toast.LENGTH_LONG).show()
                    awardFaithPoints(15)
                }
            }
        } else {
            Toast.makeText(this, "'$word' is not in the word search list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        tvProgress.text = "🔤  Words Found: ${foundWords.size} / ${targetWords.size}"

        val formattedList = targetWords.joinToString(" • ") { w ->
            if (foundWords.contains(w)) "✓ $w" else w
        }
        tvTargetList.text = "Find: $formattedList"
    }

    private fun awardFaithPoints(points: Long) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentFp = doc.getLong("faithPoints") ?: 0L
            val newFp = currentFp + points
            userRef.update("faithPoints", newFp)
                .addOnSuccessListener {
                    Log.d("WordSearchActivity", "Awarded $points FP. Total: $newFp")
                }
        }
    }
}