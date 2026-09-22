package com.example.holytextapplication

import android.content.Context

object LanguageManager {

    fun getSelectedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("holytext_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "English") ?: "English"
    }

    fun setSelectedLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("holytext_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language", language).apply()
    }

    // Translations for Settings Screen
    fun getSettingsTitle(lang: String): String = when (lang) {
        "Tshivenda" -> "⚙️ Mavhekanyelo"
        "Sepedi" -> "⚙️ Dithulaganyo"
        else -> "⚙️ Settings"
    }

    fun getLanguageLabel(lang: String): String = when (lang) {
        "Tshivenda" -> "Luambo (Language)"
        "Sepedi" -> "Polelo (Language)"
        else -> "Language"
    }

    fun getTextSizeLabel(lang: String): String = when (lang) {
        "Tshivenda" -> "Vhuhulu ha Manteṅwa (Text Size)"
        "Sepedi" -> "Bogolo bwa Lengwalo (Text Size)"
        else -> "Text Size"
    }

    fun getNotificationsLabel(lang: String): String = when (lang) {
        "Tshivenda" -> "U tusa Dzinḓivhadzo (Enable Notifications)"
        "Sepedi" -> "Kgontšha Ditsebišo (Enable Notifications)"
        else -> "Enable Notifications"
    }

    fun getSaveSettingsText(lang: String): String = when (lang) {
        "Tshivenda" -> "Vhulunga Mavhekanyelo"
        "Sepedi" -> "Boloka Dithulaganyo"
        else -> "Save Settings"
    }

    fun getLogoutText(lang: String): String = when (lang) {
        "Tshivenda" -> "Buda"
        "Sepedi" -> "Tšwa"
        else -> "Log Out"
    }

    // Translations for Home Screen
    fun getWelcomeText(lang: String, name: String): String = when (lang) {
        "Tshivenda" -> "Ṱanganedzwani, $name"
        "Sepedi" -> "O amogetšwe, $name"
        else -> "Welcome, $name"
    }

    fun getReadBibleText(lang: String): String = when (lang) {
        "Tshivenda" -> "📖  Vhala Bivhiya"
        "Sepedi" -> "📖  Bala Bibele"
        else -> "📖  Read Bible"
    }

    fun getSearchBibleText(lang: String): String = when (lang) {
        "Tshivenda" -> "🔎  I ṱoḓa Bivhiya"
        "Sepedi" -> "🔎  Nyaka ka mo Bibeleng"
        else -> "🔎  Search Bible"
    }

    fun getBookmarksText(lang: String): String = when (lang) {
        "Tshivenda" -> "🔖  Zwiswo zva Bivhiya"
        "Sepedi" -> "🔖  Tswalelo tša Bibele"
        else -> "🔖  Bookmarks & Highlights"
    }

    fun getNotesText(lang: String): String = when (lang) {
        "Tshivenda" -> "📝  Manwalwa anga"
        "Sepedi" -> "📝  Dintlha tša ka"
        else -> "📝  Personal Notes"
    }

    fun getReadingPlansText(lang: String): String = when (lang) {
        "Tshivenda" -> "📅  Pulane dza u Vhala"
        "Sepedi" -> "📅  Mano a go Bala"
        else -> "📅  Reading Plans"
    }

    fun getAudioBibleText(lang: String): String = when (lang) {
        "Tshivenda" -> "🎧  Bivhiya ya Ipfi"
        "Sepedi" -> "🎧  Bibele ya Lentšu"
        else -> "🎧  Audio Bible"
    }

    fun getTriviaText(lang: String): String = when (lang) {
        "Tshivenda" -> "🧠  Mambudziisi a Bivhiya"
        "Sepedi" -> "🧠  Dipotšišo tša Bibele"
        else -> "🧠  Bible Trivia"
    }

    fun getWordSearchText(lang: String): String = when (lang) {
        "Tshivenda" -> "🔤  U ṱoḓa Mipfi dza Bivhiya"
        "Sepedi" -> "🔤  Nyaka Mantšu a Bibele"
        else -> "🔤  Bible Word Search"
    }

    fun getStreakText(lang: String): String = when (lang) {
        "Tshivenda" -> "🔥  Duvha na Duvha"
        "Sepedi" -> "🔥  Tšwelopele ya Tšatši"
        else -> "🔥  Daily Streak"
    }

    fun getAchievementsText(lang: String): String = when (lang) {
        "Tshivenda" -> "🏆  Zwiṅwe zva vhudidini"
        "Sepedi" -> "🏆  Diphihlelelo"
        else -> "🏆  Achievements & Badges"
    }

    fun getSettingsBtnText(lang: String): String = when (lang) {
        "Tshivenda" -> "⚙️  Mavhekanyelo"
        "Sepedi" -> "⚙️  Dithulaganyo"
        else -> "⚙️  Settings"
    }
}