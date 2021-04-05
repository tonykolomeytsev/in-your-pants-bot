package com.kekmech.schedule.helpers

fun String.isVerb(): Boolean {
    return (endsWith("ай") || endsWith("уй")) && (this != "хуй")
}

fun String.getAllWords() = this
    .toLowerCase()
    .clear()
    .split("\\s+".toRegex())
    .filter { it.isNotEmpty() }
    .takeIf { it.isNotEmpty() }

private fun String.clear(): String = replace("[^0-9a-zA-Zа-яА-Я\\-\\s]".toRegex(), "")