package com.example.holytextapplication

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import java.util.Locale

class AudioBibleActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val api = BibleApiClient.service
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private lateinit var spinnerBook: Spinner
    private lateinit var etChapter: EditText
    private lateinit var tvReference: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvScripture: TextView
    private lateinit var progressBar: ProgressBar

    private var currentScriptureText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_bible)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        spinnerBook = findViewById(R.id.spinnerAudioBook)
        etChapter = findViewById(R.id.etAudioChapter)
        tvReference = findViewById(R.id.tvAudioReference)
        tvStatus = findViewById(R.id.tvAudioStatus)
        tvScripture = findViewById(R.id.tvAudioScripture)
        progressBar = findViewById(R.id.progressBarAudio)

        val btnLoad = findViewById<Button>(R.id.btnLoadAudioPassage)
        val btnPlay = findViewById<Button>(R.id.btnAudioPlay)
        val btnStop = findViewById<Button>(R.id.btnAudioStop)

        // Initialize TTS
        initTtsEngine()

        val books = arrayOf(
            "Genesis", "Exodus", "Psalms", "Proverbs",
            "Isaiah", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "Revelation"
        )
        spinnerBook.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            books
        )

        // Default Psalm 23 text
        currentScriptureText = tvScripture.text.toString()

        btnLoad.setOnClickListener {
            val book = spinnerBook.selectedItem.toString()
            val chapter = etChapter.text.toString().trim().ifEmpty { "1" }
            val query = "${book.replace(" ", "+")}+$chapter"
            loadAudioPassage(query)
        }

        btnPlay.setOnClickListener {
            playAudio()
        }

        btnStop.setOnClickListener {
            stopAudio()
        }
    }

    private fun initTtsEngine() {
        try {
            tvStatus.text = "Status: Initializing audio engine..."
            tts = TextToSpeech(applicationContext, this)
        } catch (e: Exception) {
            Log.e("AudioBibleActivity", "Failed to construct TextToSpeech", e)
            tvStatus.text = "Status: Audio engine unavailable"
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Configure Audio Attributes to stream through MEDIA
            try {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(attributes)
            } catch (e: Exception) {
                Log.e("AudioBibleActivity", "Error setting audio attributes", e)
            }

            val res1 = tts?.setLanguage(Locale.US)
            if (res1 == TextToSpeech.LANG_MISSING_DATA || res1 == TextToSpeech.LANG_NOT_SUPPORTED) {
                val res2 = tts?.setLanguage(Locale.ENGLISH)
                if (res2 == TextToSpeech.LANG_MISSING_DATA || res2 == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.getDefault())
                }
            }
            isTtsReady = true
            tvStatus.text = "Status: Audio engine ready 🎧"
            Log.d("AudioBibleActivity", "TTS Engine ready!")
        } else {
            isTtsReady = true
            tvStatus.text = "Status: Audio engine ready (standard mode) 🎧"
            Log.w("AudioBibleActivity", "TTS onInit returned status: $status")
        }
    }

    private fun loadAudioPassage(query: String) {
        progressBar.visibility = View.VISIBLE
        tvStatus.text = "Status: Loading scripture..."

        lifecycleScope.launch {
            try {
                val response = api.getPassage(query, "kjv")
                progressBar.visibility = View.GONE

                if (response.verses.isNullOrEmpty()) {
                    tvStatus.text = "Status: Passage not found"
                    Toast.makeText(this@AudioBibleActivity, "Passage not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val ref = response.reference ?: query
                val formatted = response.verses.joinToString(" ") { v ->
                    v.text?.trim() ?: ""
                }

                currentScriptureText = formatted
                tvReference.text = "🎧  $ref"
                tvScripture.text = formatted
                tvStatus.text = "Status: Passage loaded. Tap Play to listen."

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("AudioBibleActivity", "Failed to load audio passage", e)
                tvStatus.text = "Status: Failed to load passage"
            }
        }
    }

    private fun playAudio() {
        if (currentScriptureText.isEmpty()) {
            Toast.makeText(this, "No scripture text loaded to play", Toast.LENGTH_SHORT).show()
            return
        }

        checkMediaVolume()

        stopAudio()

        if (tts == null) {
            tvStatus.text = "Status: Re-initializing audio engine..."
            tts = TextToSpeech(applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    speakText(currentScriptureText)
                } else {
                    Toast.makeText(this, "Unable to initialize audio on this device", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        speakText(currentScriptureText)
    }

    private fun checkMediaVolume() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager != null) {
            val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVol == 0) {
                Toast.makeText(this, "⚠️ Media volume is muted! Please turn up device volume.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun speakText(text: String) {
        try {
            tvStatus.text = "Status: 🔊 Playing scripture audio..."
            Toast.makeText(this, "Playing Audio... 🔊", Toast.LENGTH_SHORT).show()

            val chunks = text.chunked(300)
            chunks.forEachIndexed { index, chunk ->
                val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                tts?.speak(chunk, queueMode, params, "HOLYTEXT_TTS_$index")
            }

            awardFaithPoints(5)
        } catch (e: Exception) {
            Log.e("AudioBibleActivity", "Error during TTS speak", e)
            tvStatus.text = "Status: Audio playback error"
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopAudio() {
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (e: Exception) {
            Log.e("AudioBibleActivity", "Error stopping TTS", e)
        }
        tvStatus.text = "Status: Stopped"
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

    override fun onDestroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("AudioBibleActivity", "Error shutting down TTS", e)
        }
        super.onDestroy()
    }
}