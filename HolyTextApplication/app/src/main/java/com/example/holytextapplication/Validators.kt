package com.example.holytextapplication

/**
 * Pure validation helpers for HolyText.
 * No Android dependencies — safe to test in unit tests and CI.
 */
object Validators {

    // Simple but solid email regex (no Android framework needed)
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun passwordsMatch(pw1: String, pw2: String): Boolean {
        return pw1.isNotEmpty() && pw1 == pw2
    }

    fun isValidChapter(chapter: String): Boolean {
        val n = chapter.toIntOrNull() ?: return false
        return n in 1..150
    }
}