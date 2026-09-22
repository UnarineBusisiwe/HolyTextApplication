package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TriviaActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    data class Question(
        val text: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    private val questions = listOf(
        Question(
            text = "Who was swallowed by a great fish when running away from God's command?",
            options = listOf("Moses", "Jonah", "Elijah", "Noah"),
            correctIndex = 1,
            explanation = "Correct! Jonah was swallowed by a great fish (Jonah 1:17)."
        ),
        Question(
            text = "In which city was Jesus born?",
            options = listOf("Nazareth", "Jerusalem", "Bethlehem", "Capernaum"),
            correctIndex = 2,
            explanation = "Correct! Jesus was born in Bethlehem in Judea (Matthew 2:1)."
        ),
        Question(
            text = "How many books are in the Bible in total (Old & New Testament)?",
            options = listOf("39", "66", "72", "27"),
            correctIndex = 1,
            explanation = "Correct! The Bible consists of 66 books (39 OT, 27 NT)."
        ),
        Question(
            text = "What is the shortest verse in the Bible?",
            options = listOf("Jesus wept.", "Rejoice always.", "Pray continually.", "God is love."),
            correctIndex = 0,
            explanation = "Correct! 'Jesus wept.' (John 11:35) is the shortest verse."
        ),
        Question(
            text = "Who built the Ark according to God's instructions?",
            options = listOf("Abraham", "David", "Noah", "Solomon"),
            correctIndex = 2,
            explanation = "Correct! Noah built the ark to save his family (Genesis 6:14)."
        )
    )

    private var currentIndex = 0
    private var score = 0
    private var isAnswered = false

    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button
    private lateinit var tvFeedback: TextView
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvQuestionNumber = findViewById(R.id.tvTriviaQuestionNumber)
        tvScore = findViewById(R.id.tvTriviaScore)
        tvQuestion = findViewById(R.id.tvTriviaQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)
        tvFeedback = findViewById(R.id.tvTriviaFeedback)
        btnNext = findViewById(R.id.btnNextQuestion)

        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)

        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (!isAnswered) {
                    checkAnswer(index)
                }
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                displayQuestion()
            } else {
                finishQuiz()
            }
        }

        displayQuestion()
    }

    private fun displayQuestion() {
        isAnswered = false
        val q = questions[currentIndex]

        tvQuestionNumber.text = "Question ${currentIndex + 1} of ${questions.size}"
        tvScore.text = "⭐ Score: $score"
        tvQuestion.text = q.text

        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        optionButtons.forEachIndexed { index, button ->
            button.text = q.options[index]
            button.setBackgroundColor(getColor(R.color.HolyTextApplication_primary))
            button.isEnabled = true
        }

        tvFeedback.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnNext.text = if (currentIndex == questions.size - 1) "Finish Quiz 🏆" else "Next Question ➡️"
    }

    private fun checkAnswer(selectedIndex: Int) {
        isAnswered = true
        val q = questions[currentIndex]
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)

        optionButtons.forEach { it.isEnabled = false }

        if (selectedIndex == q.correctIndex) {
            score += 10
            optionButtons[selectedIndex].setBackgroundColor(getColor(R.color.HolyTextApplication_accent))
            tvFeedback.text = "🎉 ${q.explanation}"
        } else {
            optionButtons[selectedIndex].setBackgroundColor(getColor(android.R.color.holo_red_light))
            optionButtons[q.correctIndex].setBackgroundColor(getColor(R.color.HolyTextApplication_accent))
            tvFeedback.text = "❌ Incorrect. ${q.explanation}"
        }

        tvScore.text = "⭐ Score: $score"
        tvFeedback.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
    }

    private fun finishQuiz() {
        tvQuestion.text = "Quiz Completed! 🏆\nYour final score: $score points"
        tvQuestionNumber.text = "Finished"
        listOf(btnOption1, btnOption2, btnOption3, btnOption4).forEach { it.visibility = View.GONE }
        tvFeedback.visibility = View.GONE

        btnNext.text = "Play Again 🔄"
        btnNext.setOnClickListener {
            currentIndex = 0
            score = 0
            listOf(btnOption1, btnOption2, btnOption3, btnOption4).forEach { it.visibility = View.VISIBLE }
            displayQuestion()
        }

        Toast.makeText(this, "Quiz completed! +15 Faith Points added!", Toast.LENGTH_LONG).show()
        awardFaithPoints(15)
    }

    private fun awardFaithPoints(points: Long) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentFp = doc.getLong("faithPoints") ?: 0L
            val newFp = currentFp + points
            userRef.update("faithPoints", newFp)
                .addOnSuccessListener {
                    Log.d("TriviaActivity", "Awarded $points FP. Total: $newFp")
                }
        }
    }
}