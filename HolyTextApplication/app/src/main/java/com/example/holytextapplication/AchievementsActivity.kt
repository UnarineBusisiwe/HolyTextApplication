package com.example.holytextapplication

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AchievementsActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var tvFaithPointsTotal: TextView
    private lateinit var tvUnlockedCount: TextView
    private lateinit var containerAchievements: LinearLayout

    data class Achievement(
        val icon: String,
        val title: String,
        val description: String,
        val targetPointsOrStreak: Long,
        val isStreakBased: Boolean = false
    )

    private val achievementsList = listOf(
        Achievement("📖", "First Steps in Scripture", "Read your first Bible passage or search a verse", 5L),
        Achievement("🔥", "Streak Starter", "Maintain a 3-day daily scripture reading streak", 3L, isStreakBased = true),
        Achievement("⭐", "Faith Explorer", "Earn 25 Faith Points through study & reflections", 25L),
        Achievement("🧠", "Wisdom Seeker", "Earn 50 Faith Points by completing trivia & word search", 50L),
        Achievement("🏆", "Devoted Scholar", "Earn 100 Faith Points", 100L),
        Achievement("👑", "Faith Warrior", "Earn 200 Faith Points", 200L)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvFaithPointsTotal = findViewById(R.id.tvFaithPointsTotal)
        tvUnlockedCount = findViewById(R.id.tvUnlockedCount)
        containerAchievements = findViewById(R.id.containerAchievements)

        loadUserDataAndRenderBadges()
    }

    private fun loadUserDataAndRenderBadges() {
        val user = auth.currentUser
        if (user == null) {
            renderBadges(0L, 0L)
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val streak = doc.getLong("streak") ?: 0L
                val fp = doc.getLong("faithPoints") ?: 0L
                renderBadges(fp, streak)
            }
            .addOnFailureListener { e ->
                Log.e("AchievementsActivity", "Failed to load achievements", e)
                renderBadges(0L, 0L)
            }
    }

    private fun renderBadges(faithPoints: Long, streak: Long) {
        tvFaithPointsTotal.text = "⭐ Total Faith Points: $faithPoints FP"
        containerAchievements.removeAllViews()

        var unlockedCount = 0

        achievementsList.forEach { ach ->
            val currentValue = if (ach.isStreakBased) streak else faithPoints
            val isUnlocked = currentValue >= ach.targetPointsOrStreak

            if (isUnlocked) unlockedCount++

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

            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val tvIconTitle = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "${ach.icon}  ${ach.title}"
                textSize = 17f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(getColor(R.color.HolyTextApplication_primary))
            }

            val tvBadgeStatus = TextView(this).apply {
                text = if (isUnlocked) "UNLOCKED 🏆" else "LOCKED 🔒"
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(
                    if (isUnlocked) getColor(R.color.HolyTextApplication_accent)
                    else getColor(R.color.HolyTextApplication_text_secondary)
                )
            }

            topRow.addView(tvIconTitle)
            topRow.addView(tvBadgeStatus)

            val tvDesc = TextView(this).apply {
                text = ach.description
                textSize = 14f
                setTextColor(getColor(R.color.HolyTextApplication_text_primary))
                setPadding(0, 8, 0, 12)
            }

            val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = ach.targetPointsOrStreak.toInt()
                this.progress = currentValue.coerceAtMost(ach.targetPointsOrStreak).toInt()
                progressTintList = android.content.res.ColorStateList.valueOf(
                    if (isUnlocked) getColor(R.color.HolyTextApplication_accent)
                    else getColor(R.color.HolyTextApplication_primary)
                )
            }

            val tvProgressLabel = TextView(this).apply {
                text = "Progress: ${currentValue.coerceAtMost(ach.targetPointsOrStreak)} / ${ach.targetPointsOrStreak}"
                textSize = 12f
                setTextColor(getColor(R.color.HolyTextApplication_text_secondary))
                setPadding(0, 6, 0, 0)
            }

            layout.addView(topRow)
            layout.addView(tvDesc)
            layout.addView(progress)
            layout.addView(tvProgressLabel)
            card.addView(layout)

            containerAchievements.addView(card)
        }

        tvUnlockedCount.text = "Badges Unlocked: $unlockedCount / ${achievementsList.size}"
    }
}