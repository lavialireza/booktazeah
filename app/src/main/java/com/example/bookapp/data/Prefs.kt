package com.example.bookapp.data

import android.content.Context

/**
 * ذخیره‌سازی ساده تنظیمات کاربر (حالت روز/شب و سایز فونت) با SharedPreferences.
 */
object Prefs {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_FONT_SCALE = "font_scale" // 0.85f=کوچک, 1.0f=متوسط, 1.3f=بزرگ

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getFontScale(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_FONT_SCALE, 1.0f)
    }

    fun setFontScale(context: Context, scale: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_FONT_SCALE, scale).apply()
    }

    private const val KEY_BOOKMARKS = "bookmarks"

    fun getBookmarks(context: Context): Set<Long> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_BOOKMARKS, emptySet())?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()
    }

    fun isBookmarked(context: Context, sectionId: Long): Boolean {
        return getBookmarks(context).contains(sectionId)
    }

    fun toggleBookmark(context: Context, sectionId: Long): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getBookmarks(context).toMutableSet()
        val nowBookmarked: Boolean
        if (current.contains(sectionId)) {
            current.remove(sectionId)
            nowBookmarked = false
        } else {
            current.add(sectionId)
            nowBookmarked = true
        }
        prefs.edit().putStringSet(KEY_BOOKMARKS, current.map { it.toString() }.toSet()).apply()
        return nowBookmarked
    }

    private const val KEY_RECENT = "recent_sections"
    private const val MAX_RECENT = 10

    /** لیست شناسه‌های اخیراً مشاهده‌شده را از جدید به قدیم برمی‌گرداند */
    fun getRecent(context: Context): List<Long> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_RECENT, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(",").mapNotNull { it.toLongOrNull() }
    }

    fun addRecent(context: Context, sectionId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getRecent(context).toMutableList()
        current.remove(sectionId)
        current.add(0, sectionId)
        val trimmed = current.take(MAX_RECENT)
        prefs.edit().putString(KEY_RECENT, trimmed.joinToString(",")).apply()
    }

    private const val KEY_THEME = "theme_choice"

    /** یکی از سه مقدار: "default"، "green"، "red" */
    fun getThemeChoice(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, "default") ?: "default"
    }

    fun setThemeChoice(context: Context, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, value).apply()
    }

    private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"

    fun isOnboardingShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_SHOWN, false)
    }

    fun setOnboardingShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_SHOWN, true).apply()
    }

    private const val KEY_FONT_CHOICE = "font_choice"

    /** یکی از مقادیر "titr"، "serif"، "sans"، "cursive" */
    fun getFontChoice(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FONT_CHOICE, "titr") ?: "titr"
    }

    fun setFontChoice(context: Context, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FONT_CHOICE, value).apply()
    }
}
