package com.example.holytextapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReadingPlansActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var tvPlanProgress: TextView
    private lateinit var progressBarPlan: ProgressBar
    private lateinit var containerDays: LinearLayout

    private val planDays = listOf(
        "Day 1" to "Proverbs 1 - The Beginning of Knowledge",
        "Day 2" to "Proverbs 2 - Value of Wisdom",
        "Day 3" to "Proverbs 3 - Trust in the Lord",
        "Day 4" to "Proverbs 4 - Get Wisdom & Understanding",
        "Day 5" to "Proverbs 15 - A Soft Answer Turns Away Wrath",
        "Day 6" to "Proverbs 16 - Commit to the Lord",
        "Day 7" to "Proverbs 31 - The Virtuous Life"
    )

    private val completedDays = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_plans)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvPlanProgress = findViewById(R.id.tvPlanProgress)
        progressBarPlan = findViewById(R.id.progressBarPlan)
        containerDays = findViewById(R.id.containerPlanDays)

        loadPlanProgress()
    }

    private fun loadPlanProgress() {
        val user = auth.currentUser
        if (user == null) {
            renderDays()
            return
        }

        db.collection("users")
            .document(user.uid)
            .collection("readingPlans")
            .document("proverbs7")
            .get()
            .addOnSuccessListener { doc ->
                completedDays.clear()
                val list = doc.get("completedDays") as? List<*>
                list?.forEach { day ->
                    if (day is Long) completedDays.add(day.toInt())
                }
                updateProgressUI()
                renderDays()
            }
            .addOnFailureListener { e ->
                Log.e("ReadingPlansActivity", "Failed to load plan", e)
                renderDays()
            }
    }

    private fun updateProgressUI() {
        val count = completedDays.size
        val total = planDays.size
        val percent = (count * 100) / total

        tvPlanProgress.text = "Progress: $count / $total Days Completed ($percent%)"
        progressBarPlan.progress = count
    }

    private fun renderDays() {
        containerDays.removeAllViews()

        planDays.forEachIndexed { index, (dayLabel, passage) ->
            val isDone = completedDays.contains(index + 1)

            val card = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 20)
                }
                radius = 24f
                cardElevation = 4f
                setCardBackgroundColor(getColor(R.color.HolyTextApplication_surface))
            }

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(28, 28, 28, 28)
            }

            val tvDayHeader = TextView(this).apply {
                text = if (isDone) "✅ $dayLabel (Completed)" else "📖 $dayLabel"
                textSize = 17f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(
                    if (isDone) getColor(R.color.HolyTextApplication_accent)
                    else getColor(R.color.HolyTextApplication_primary)
                )
            }

            val tvPassageText = TextView(this).apply {
                text = passage
                textSize = 15f
                setTextColor(getColor(R.color.HolyTextApplication_text_primary))
                setPadding(0, 8, 0, 16)
            }

            val buttonLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val btnRead = Button(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(0, 0, 8, 0)
                }
                text = "Read Chapter"
                isAllCaps = false
                setBackgroundColor(getColor(R.color.HolyTextApplication_primary))
                setTextColor(getColor(R.color.HolyTextApplication_white))
                setOnClickListener {
                    startActivity(Intent(this@ReadingPlansActivity, BibleReaderActivity::class.java))
                }
            }

            val btnToggle = Button(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(8, 0, 0, 0)
                }
                text = if (isDone) "Mark Incomplete" else "Mark Complete"
                isAllCaps = false
                setBackgroundColor(
                    if (isDone) getColor(R.color.HolyTextApplication_accent)
                    else getColor(R.color.HolyTextApplication_primary)
                )
                setTextColor(getColor(R.color.HolyTextApplication_white))
                setOnClickListener {
                    toggleDay(index + 1)
                }
            }

            buttonLayout.addView(btnRead)
            buttonLayout.addView(btnToggle)

            layout.addView(tvDayHeader)
            layout.addView(tvPassageText)
            layout.addView(buttonLayout)
            card.addView(layout)

            containerDays.addView(card)
        }
    }

    private fun toggleDay(dayNumber: Int) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show()
            return
        }

        if (completedDays.contains(dayNumber)) {
            completedDays.remove(dayNumber)
        } else {
            completedDays.add(dayNumber)
            Toast.makeText(this, "Day $dayNumber Completed! 🎉 (+10 FP)", Toast.LENGTH_SHORT).show()
            awardFaithPoints(10)
        }

        db.collection("users")
            .document(user.uid)
            .collection("readingPlans")
            .document("proverbs7")
            .set(mapOf("completedDays" to completedDays.toList()))
            .addOnSuccessListener {
                updateProgressUI()
                renderDays()
            }
            .addOnFailureListener { e ->
                Log.e("ReadingPlansActivity", "Failed to update plan", e)
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