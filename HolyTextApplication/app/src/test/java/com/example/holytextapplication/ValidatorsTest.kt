package com.example.holytextapplication

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    // ---------- Email ----------
    @Test
    fun validEmail_returnsTrue() {
        assertTrue(Validators.isValidEmail("test@example.com"))
    }

    @Test
    fun invalidEmail_returnsFalse() {
        assertFalse(Validators.isValidEmail("not-an-email"))
    }

    @Test
    fun emptyEmail_returnsFalse() {
        assertFalse(Validators.isValidEmail(""))
    }

    // ---------- Password ----------
    @Test
    fun shortPassword_returnsFalse() {
        assertFalse(Validators.isValidPassword("123"))
    }

    @Test
    fun validPassword_returnsTrue() {
        assertTrue(Validators.isValidPassword("secret123"))
    }

    // ---------- Password match ----------
    @Test
    fun matchingPasswords_returnsTrue() {
        assertTrue(Validators.passwordsMatch("abc123", "abc123"))
    }

    @Test
    fun mismatchedPasswords_returnsFalse() {
        assertFalse(Validators.passwordsMatch("abc123", "different"))
    }

    // ---------- Chapter number ----------
    @Test
    fun validChapter_returnsTrue() {
        assertTrue(Validators.isValidChapter("3"))
        assertTrue(Validators.isValidChapter("150"))
    }

    @Test
    fun invalidChapter_returnsFalse() {
        assertFalse(Validators.isValidChapter("0"))
        assertFalse(Validators.isValidChapter("151"))
        assertFalse(Validators.isValidChapter("abc"))
    }
}