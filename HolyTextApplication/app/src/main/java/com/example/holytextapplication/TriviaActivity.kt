package com.example.holytextapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class TriviaQuestion(
    val questionEn: String,
    val questionSep: String,
    val questionVen: String,
    val optionsEn: List<String>,
    val optionsSep: List<String>,
    val optionsVen: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationSep: String,
    val explanationVen: String
)

class TriviaActivity : AppCompatActivity() {

    private var currentLanguage: String = "English"
    private var currentQuestionIndex = 0
    private var score = 0
    private var totalFpEarned = 0
    private var isAnswered = false

    private lateinit var containerQuiz: View
    private lateinit var containerResult: View
    private lateinit var tvQuestionProgress: TextView
    private lateinit var tvFaithPointsEarned: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var tvQuestionText: TextView
    private lateinit var btnOptionA: Button
    private lateinit var btnOptionB: Button
    private lateinit var btnOptionC: Button
    private lateinit var btnOptionD: Button
    private lateinit var optionButtons: List<Button>
    private lateinit var cardFeedback: CardView
    private lateinit var tvFeedbackTitle: TextView
    private lateinit var tvFeedbackExplanation: TextView
    private lateinit var btnNextQuestion: Button

    private lateinit var tvResultScore: TextView
    private lateinit var tvResultFaithPoints: TextView
    private lateinit var btnPlayAgain: Button
    private lateinit var btnBackHome: Button

    private val questions = listOf(
        TriviaQuestion(
            questionEn = "Who built the Ark according to God's command?",
            questionSep = "Ke mang yo a hlodilego areka go ya ka taelo ya Modimo?",
            questionVen = "Ndi nnyi we a fhaṱa tshiṱimbi a tshi tevhedza mulao wa Mudzimu?",
            optionsEn = listOf("Noah", "Moses", "Abraham", "David"),
            optionsSep = listOf("Noa", "Moše", "Abrahama", "Dafida"),
            optionsVen = listOf("Noaxe", "Mushe", "Abarahama", "Davhita"),
            correctIndex = 0,
            explanationEn = "Noah built the ark as commanded by God in Genesis 6:14.",
            explanationSep = "Noa o hlodile areka bjalo ka ge Modimo a laotše go Genese 6:14.",
            explanationVen = "Noaxe o fhaṱa tshiṱimbi sa we Mudzimu a laedza kha Genesisi 6:14."
        ),
        TriviaQuestion(
            questionEn = "How many disciples did Jesus choose?",
            questionSep = "Jeso o kgethile barutwana ba ba kae?",
            questionVen = "Yesu o nanga vhafunda vhangaana?",
            optionsEn = listOf("7", "10", "12", "40"),
            optionsSep = listOf("7", "10", "12", "40"),
            optionsVen = listOf("7", "10", "12", "40"),
            correctIndex = 2,
            explanationEn = "Jesus chose 12 apostles as recorded in Matthew 10:2-4.",
            explanationSep = "Jeso o kgethile baapostola ba 12 go Mattheo 10:2-4.",
            explanationVen = "Yesu o nanga vhavenda vha 12 kha Mateo 10:2-4."
        ),
        TriviaQuestion(
            questionEn = "What is the first book of the Bible?",
            questionSep = "Puku ya pele ya Bibele ke efe?",
            questionVen = "Bugu ya u thoma ya Bivhili ndi ifhio?",
            optionsEn = listOf("Genesis", "Exodus", "Psalms", "Matthew"),
            optionsSep = listOf("Genese", "Eksodo", "Dipsalme", "Mattheo"),
            optionsVen = listOf("Genesisi", "Eksodo", "Pisalema", "Mateo"),
            correctIndex = 0,
            explanationEn = "Genesis is the first book of both the Old Testament and the Bible.",
            explanationSep = "Genese ke puku ya pele ya Testamente e Tala le ya Bibele.",
            explanationVen = "Genesisi ndi bugu ya u thoma ya Bivhili."
        ),
        TriviaQuestion(
            questionEn = "In which town was Jesus born?",
            questionSep = "Jeso o tswaletswe motseng ofe?",
            questionVen = "Yesu o bevhwa ḓoroboni ḓifhio?",
            optionsEn = listOf("Jerusalem", "Bethlehem", "Nazareth", "Jericho"),
            optionsSep = listOf("Jerusalema", "Bethelehema", "Nasaretha", "Jeriko"),
            optionsVen = listOf("Yerusalema", "Bethelehema", "Nazareta", "Yeriko"),
            correctIndex = 1,
            explanationEn = "Jesus was born in Bethlehem as prophesied in Micah 5:2 and Luke 2:4.",
            explanationSep = "Jeso o tswaletswe Bethelehema go ya ka moporofeta Mika 5:2 le Luka 2:4.",
            explanationVen = "Yesu o bevhwa Bethelehema sa we zva mporofita kha Mika 5:2 na Luka 2:4."
        ),
        TriviaQuestion(
            questionEn = "Who received the Ten Commandments on Mount Sinai?",
            questionSep = "Ke mang yo a amogetsego Melawo e Lesome Thabeng ya Sinai?",
            questionVen = "Ndi nnyi we a ṱanganedza Mulao wa Muvhili wa Fumi Thavhani ya Sinai?",
            optionsEn = listOf("Joshua", "Moses", "Aaron", "Elijah"),
            optionsSep = listOf("Joshua", "Moše", "Aarone", "Elia"),
            optionsVen = listOf("Yosua", "Mushe", "Aroni", "Eliya"),
            correctIndex = 1,
            explanationEn = "God gave the Ten Commandments to Moses on Mount Sinai in Exodus 20.",
            explanationSep = "Modimo o neile Moše Melawo e Lesome Thabeng ya Sinai go Eksodo 20.",
            explanationVen = "Mudzimu o ṋea Mushe Mulao wa Fumi Thavhani ya Sinai kha Eksodo 20."
        ),
        TriviaQuestion(
            questionEn = "Who defeated Goliath with a sling and a stone?",
            questionSep = "Ke mang yo a fentsego Goliate ka seruthe le lswika?",
            questionVen = "Ndi nnyi we a kunda Goliatha nga lutaka na tombo?",
            optionsEn = listOf("Saul", "David", "Solomon", "Samson"),
            optionsSep = listOf("Saule", "Dafida", "Solomone", "Samagson"),
            optionsVen = listOf("Saulo", "Davhita", "Solomoni", "Samusoni"),
            correctIndex = 1,
            explanationEn = "David defeated Goliath with a sling and stone in 1 Samuel 17.",
            explanationSep = "Dafida o fentse Goliate ka seruthe le lswika go 1 Samuele 17.",
            explanationVen = "Davhita o kunda Goliatha nga lutaka na tombo kha 1 Samuele 17."
        ),
        TriviaQuestion(
            questionEn = "What is the last book of the New Testament?",
            questionSep = "Puku ya mafelelo ya Testamente e Mpsha ke efe?",
            questionVen = "Bugu ya u fhedza ya Bivhili ndi ifhio?",
            optionsEn = listOf("Jude", "Acts", "Revelation", "Genesis"),
            optionsSep = listOf("Juda", "Ditiro", "Kutollo", "Genese"),
            optionsVen = listOf("Yuda", "Mavhudzi", "Nzumbululo", "Genesisi"),
            correctIndex = 2,
            explanationEn = "Revelation is the final book of the New Testament.",
            explanationSep = "Kutollo ke puku ya mafelelo ya Testamente e Mpsha.",
            explanationVen = "Nzumbululo ndi bugu ya u fhedza ya Bivhili."
        ),
        TriviaQuestion(
            questionEn = "Who was swallowed by a great fish when fleeing from God?",
            questionSep = "Ke mang yo a meditsego ke hlapi e kgolo ge a tšhaba Modimo?",
            questionVen = "Ndi nnyi we a miziwa nga khovhe khulu musi a tshi shavha Mudzimu?",
            optionsEn = listOf("Elijah", "Jonah", "Daniel", "Job"),
            optionsSep = listOf("Elia", "Jona", "Daniele", "Jobo"),
            optionsVen = listOf("Eliya", "Yona", "Daniele", "Yobo"),
            correctIndex = 1,
            explanationEn = "Jonah was swallowed by a great fish in Jonah 1:17.",
            explanationSep = "Jona o meditswe ke hlapi e kgolo go Jona 1:17.",
            explanationVen = "Yona o miziwa nga khovhe khulu kha Yona 1:17."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia)

        // Read user's language setting
        val prefs = getSharedPreferences("holytext_prefs", MODE_PRIVATE)
        currentLanguage = prefs.getString("language", "English") ?: "English"

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.subtitle = "Language: $currentLanguage"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        containerQuiz = findViewById(R.id.containerQuiz)
        containerResult = findViewById(R.id.containerResult)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        tvFaithPointsEarned = findViewById(R.id.tvFaithPointsEarned)
        progressQuiz = findViewById(R.id.progressQuiz)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        btnOptionA = findViewById(R.id.btnOptionA)
        btnOptionB = findViewById(R.id.btnOptionB)
        btnOptionC = findViewById(R.id.btnOptionC)
        btnOptionD = findViewById(R.id.btnOptionD)
        optionButtons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
        cardFeedback = findViewById(R.id.cardFeedback)
        tvFeedbackTitle = findViewById(R.id.tvFeedbackTitle)
        tvFeedbackExplanation = findViewById(R.id.tvFeedbackExplanation)
        btnNextQuestion = findViewById(R.id.btnNextQuestion)

        tvResultScore = findViewById(R.id.tvResultScore)
        tvResultFaithPoints = findViewById(R.id.tvResultFaithPoints)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)
        btnBackHome = findViewById(R.id.btnBackHome)

        for (i in optionButtons.indices) {
            optionButtons[i].setOnClickListener {
                if (!isAnswered) {
                    checkAnswer(i)
                }
            }
        }

        btnNextQuestion.setOnClickListener {
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                displayQuestion()
            } else {
                showResults()
            }
        }

        btnPlayAgain.setOnClickListener {
            currentQuestionIndex = 0
            score = 0
            totalFpEarned = 0
            containerResult.visibility = View.GONE
            containerQuiz.visibility = View.VISIBLE
            displayQuestion()
        }

        btnBackHome.setOnClickListener {
            finish()
        }

        displayQuestion()
    }

    private fun displayQuestion() {
        isAnswered = false
        cardFeedback.visibility = View.GONE
        btnNextQuestion.visibility = View.GONE

        val q = questions[currentQuestionIndex]

        tvQuestionProgress.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
        tvFaithPointsEarned.text = "⭐ $totalFpEarned FP"
        val progressPercent = ((currentQuestionIndex + 1) * 100) / questions.size
        progressQuiz.progress = progressPercent

        val questionStr = when (currentLanguage.lowercase()) {
            "sepedi" -> q.questionSep
            "tshivenda" -> q.questionVen
            else -> q.questionEn
        }

        val optionsList = when (currentLanguage.lowercase()) {
            "sepedi" -> q.optionsSep
            "tshivenda" -> q.optionsVen
            else -> q.optionsEn
        }

        tvQuestionText.text = questionStr

        val prefixes = listOf("A. ", "B. ", "C. ", "D. ")
        for (i in optionButtons.indices) {
            val btn = optionButtons[i]
            btn.text = "${prefixes[i]}${optionsList[i]}"
            btn.setBackgroundColor(Color.WHITE)
            btn.setTextColor(Color.parseColor("#1F2937"))
            btn.isEnabled = true
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        isAnswered = true
        val q = questions[currentQuestionIndex]
        val isCorrect = (selectedIndex == q.correctIndex)

        for (i in optionButtons.indices) {
            val btn = optionButtons[i]
            btn.isEnabled = false
            if (i == q.correctIndex) {
                btn.setBackgroundColor(Color.parseColor("#10B981")) // Emerald Green
                btn.setTextColor(Color.WHITE)
            } else if (i == selectedIndex && !isCorrect) {
                btn.setBackgroundColor(Color.parseColor("#EF4444")) // Red
                btn.setTextColor(Color.WHITE)
            }
        }

        val explanationStr = when (currentLanguage.lowercase()) {
            "sepedi" -> q.explanationSep
            "tshivenda" -> q.explanationVen
            else -> q.explanationEn
        }

        cardFeedback.visibility = View.VISIBLE
        if (isCorrect) {
            score++
            totalFpEarned += 10
            awardFaithPoints(10)
            tvFeedbackTitle.text = "Correct! 🎉 (+10 FP)"
            tvFeedbackTitle.setTextColor(Color.parseColor("#10B981"))
        } else {
            val correctOptionLetter = when (q.correctIndex) {
                0 -> "A"
                1 -> "B"
                2 -> "C"
                else -> "D"
            }
            tvFeedbackTitle.text = "Incorrect (Correct is $correctOptionLetter) ❌"
            tvFeedbackTitle.setTextColor(Color.parseColor("#EF4444"))
        }

        tvFeedbackExplanation.text = explanationStr
        tvFaithPointsEarned.text = "⭐ $totalFpEarned FP"
        btnNextQuestion.visibility = View.VISIBLE
    }

    private fun showResults() {
        containerQuiz.visibility = View.GONE
        containerResult.visibility = View.VISIBLE

        tvResultScore.text = "Score: $score / ${questions.size}"
        tvResultFaithPoints.text = "⭐ +$totalFpEarned Faith Points Earned!"
        Toast.makeText(this, "Trivia Complete! Earned +$totalFpEarned FP", Toast.LENGTH_LONG).show()
    }

    private fun awardFaithPoints(points: Long) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val currentFp = doc.getLong("faithPoints") ?: 0L
                val newFp = currentFp + points

                db.collection("users").document(user.uid)
                    .update("faithPoints", newFp)
                    .addOnSuccessListener {
                        Log.d("TriviaActivity", "Awarded $points FP. New total: $newFp")
                    }
                    .addOnFailureListener { e ->
                        Log.e("TriviaActivity", "Failed to award FP", e)
                    }
            }
    }
}