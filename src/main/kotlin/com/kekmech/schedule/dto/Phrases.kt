package com.kekmech.schedule.dto

data class Phrases(
    val actualForOneWord: Map<String, List<Formattable>>,
    val randomPhrasesNouns: List<Formattable>,
    val randomPhrasesVerbs: List<Formattable>
)