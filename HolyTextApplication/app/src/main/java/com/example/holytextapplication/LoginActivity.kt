
package com.example.holytextapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: Button
    private lateinit var registerLink: TextView

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.etLoginEmail)
        passwordInput = findViewById(R.id.etLoginPassword)
        loginButton = findViewById(R.id.btnLogin)
        googleSignInButton = findViewById(R.id.btnGoogleSignIn)
        registerLink = findViewById(R.id.tvRegister)

        // Email and password login
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

        // Google Sign-In
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Registration navigation
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
                    Log.d(
                        "LoginActivity",
                        "Login success. UID=${task.result?.user?.uid}"
                    )

                    Toast.makeText(
                        this,
                        "Welcome back!",
                        Toast.LENGTH_SHORT
                    ).show()

                    openHome()
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

    private fun signInWithGoogle() {
        googleSignInButton.isEnabled = false
        googleSignInButton.text = "Connecting to Google..."

        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(
                        getString(R.string.default_web_client_id)
                    )
                    .setFilterByAuthorizedAccounts(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    firebaseAuthWithGoogle(googleCredential.idToken)
                } else {
                    throw IllegalStateException(
                        "Unexpected Google credential type"
                    )
                }

            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Google sign-in failed", e)

                googleSignInButton.isEnabled = true
                googleSignInButton.text = "Continue with Google"

                Toast.makeText(
                    this@LoginActivity,
                    e.localizedMessage ?: "Google sign-in was cancelled or failed",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Log.e("LoginActivity", "Google sign-in error", e)

                googleSignInButton.isEnabled = true
                googleSignInButton.text = "Continue with Google"

                Toast.makeText(
                    this@LoginActivity,
                    e.localizedMessage ?: "Something went wrong",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential =
            GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->

                googleSignInButton.isEnabled = true
                googleSignInButton.text = "Continue with Google"

                if (task.isSuccessful) {
                    val user = auth.currentUser

                    Log.d(
                        "LoginActivity",
                        "Google login successful. UID=${user?.uid}"
                    )

                    Toast.makeText(
                        this,
                        "Welcome ${user?.displayName ?: ""}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    openHome()

                } else {
                    Log.e(
                        "LoginActivity",
                        "Firebase Google authentication failed",
                        task.exception
                    )

                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage
                            ?: "Google authentication failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
