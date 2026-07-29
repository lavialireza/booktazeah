package com.example.bookapp.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * پوششی ساده روی TextToSpeech اندروید برای خواندن متن اشعار با صدای مصنوعی،
 * بدون نیاز به فایل صوتی ضبط‌شده.
 */
class SpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ready = true
                val result = tts?.setLanguage(Locale("fa", "IR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // اگر زبان فارسی روی گوشی نصب نبود، از تنظیم پیش‌فرض دستگاه استفاده می‌شود
                    tts?.setLanguage(Locale.getDefault())
                }
            }
        }
    }

    fun speak(text: String) {
        if (ready) {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tazieh_tts")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
