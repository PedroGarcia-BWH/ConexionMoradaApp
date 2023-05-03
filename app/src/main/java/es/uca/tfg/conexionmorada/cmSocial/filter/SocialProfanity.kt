package es.uca.tfg.conexionmorada.cmSocial.filter


import android.content.Context
import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetector
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import com.modernmt.text.profanity.ProfanityFilter
import com.modernmt.text.profanity.dictionary.Profanity
import java.io.File
import java.io.FileInputStream


class SocialProfanity {
    private val filter = ProfanityFilter()

    fun replaceProfanity(text: String): String {
        var outputText = text.lowercase()

        var lang = detectLanguage(outputText)

        if(lang == "ENGLISH") lang = "en"
        else if(lang == "FRENCH") lang = "fr"
        else if(lang == "GERMAN") lang = "de"
        else if(lang == "SPANISH") lang = "es"



        while (filter.find(lang, outputText) != null ) {
            val profanity: Profanity = filter.find(lang, outputText)
            outputText = outputText.replace(profanity.text(), "*".repeat(profanity.text().length))
        }

        //return outputText + lang
        return outputText
    }

    fun detectLanguage(text: String): String {

        val detector: LanguageDetector = LanguageDetectorBuilder.fromLanguages(Language.ENGLISH,
            Language.FRENCH, Language.GERMAN, Language.SPANISH
        ).build()
        val detectedLanguage: Language = detector.detectLanguageOf(text)

        return detectedLanguage.toString()
    }


}

