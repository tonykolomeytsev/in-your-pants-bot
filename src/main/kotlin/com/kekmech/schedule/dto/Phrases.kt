package com.kekmech.schedule.dto

data class Phrases(
    val actualForOneWord: Map<String, List<Formattable>>,
    val randomPhrasesNouns: List<Formattable>,
    val randomPhrasesVerbs: List<Formattable>
)

data class Formattable(val string: String) {
    fun format(text: String) = string.replace("{}", text).capitalize()
}