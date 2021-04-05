package com.kekmech.schedule.dto

data class Formattable(val string: String) {
    fun format(text: String) = string.replace("{}", text).capitalize()
}