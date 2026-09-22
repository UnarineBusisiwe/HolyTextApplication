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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.etLoginEmail)
        passwordInput = findViewById(R.id.etLoginPassword)
        loginButton = findViewById(R.id.btnLogin)
        registerLink = findViewById(R.id.tvRegister)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isEmpty()) {
                emailInput.error = "Please enter your email"
                emailInput.requestFocus()
                return@setOnClickListener
            }
            if (!Validators.isValidEmail(email)) {
                emailInput.error = "Invalid email format"
                emailInput.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInput.error = "Please enter your password"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            performLogin(email, password)
        }

        registerLink.setOnClickListener {
            Log.d("LoginActivity", "Navigate to Register")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        Log.d("LoginActivity", "Attempting login for $email")
        loginButton.isEnabled = false
        loginButton.text = "Signing in…"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Login success. UID=${task.result?.user?.uid}")
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Log.e("LoginActivity", "Login failed", task.exception)
                    loginButton.isEnabled = true
                    loginButton.text = "Sign In"
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}