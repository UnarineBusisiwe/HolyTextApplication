package com.example.holytextapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private fun getTodayDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    // Lazy instances – created only when first used
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameInput = findViewById(R.id.etName)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        confirmPasswordInput = findViewById(R.id.etConfirmPassword)
        registerButton = findViewById(R.id.btnRegister)
        loginLink = findViewById(R.id.tvLogin)

        registerButton.setOnClickListener {
            validateRegistration()
        }

        // Navigate to Login when "Already have an account?" is tapped
        loginLink.setOnClickListener {
            Log.d("RegisterActivity", "Navigate to Login")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateRegistration() {

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (name.isEmpty()) {
            nameInput.error = "Please enter your full name"
            nameInput.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailInput.error = "Please enter your email address"
            emailInput.requestFocus()
            return
        }



        if (!Validators.isValidEmail(email)) {
            emailInput.error = "Please enter a valid email address"
            emailInput.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Please enter a password"
            passwordInput.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordInput.error = "Password must contain at least 6 characters"
            passwordInput.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error = "Please confirm your password"
            confirmPasswordInput.requestFocus()
            return
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            confirmPasswordInput.requestFocus()
            return
        }

        // ✅ All valid – register with Firebase
        registerUser(name, email, password)
    }

    private fun registerUser(name: String, email: String, password: String) {
        Log.d("RegisterActivity", "Attempting registration for $email")

        registerButton.isEnabled = false
        registerButton.text = "Creating account…"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    Log.d("RegisterActivity", "Auth success. UID=$uid")

                    if (uid != null) {
                        saveProfileToFirestore(uid, name, email)
                    } else {
                        onRegisterFailed("User ID not returned")
                    }
                } else {
                    Log.e("RegisterActivity", "Auth failed", task.exception)
                    onRegisterFailed(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun saveProfileToFirestore(uid: String, name: String, email: String) {
        val profile = mapOf(
            "name" to name,
            "email" to email,
            "preferredLanguage" to "English",
            "notificationsEnabled" to true,
            "streak" to 1,
            "faithPoints" to 10,
            "lastActiveDate" to getTodayDate(),
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid)
            .set(profile)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Profile saved to Firestore")
                Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Firestore write failed", e)
                Toast.makeText(
                    this,
                    "Account created, but profile save failed.",
                    Toast.LENGTH_LONG
                ).show()
                // Still let user in – auth was successful
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }

    private fun onRegisterFailed(message: String) {
        registerButton.isEnabled = true
        registerButton.text = "Create Account"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}