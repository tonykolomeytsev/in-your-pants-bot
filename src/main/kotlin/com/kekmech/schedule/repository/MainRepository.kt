package com.kekmech.schedule.repository

import com.kekmech.schedule.dto.Phrases
import com.kekmech.schedule.dto.VKMessage
import com.kekmech.schedule.dto.VKMessagePeerType.GROUP_CHAT
import com.kekmech.schedule.helpers.MessageCounter
import com.kekmech.schedule.helpers.getAllWords
import com.kekmech.schedule.helpers.isVerb
import com.kekmech.schedule.repository.sources.PhrasesSource
import kotlin.random.Random

class MainRepository(
    private val phrasesSource: PhrasesSource,
    private val messageCounter: MessageCounter
) {

    fun getPhrase(message: VKMessage): String? {
        if (message.peerType == GROUP_CHAT) {
            messageCounter.incrementCounterWith(message.peerId)
            if (!messageCounter.isTimeForAnswer(message.peerId)) {
                return null
            }
        }

        val words = message.text?.getAllWords() ?: return null
        val dict = phrasesSource.getPhrases() ?: return null

        when {
            checkLinks(words) -> return null
            checkMentioning(words) -> return dict.actualForOneWord["привет"]?.random()?.format("")
        }

        val response = if (words.size == 1) {
            val word = words.first()
            dict.actualForOneWord[word]?.random()?.format(word).randomTake(0.90)
                ?: getRandomPhrase(dict, word).randomTake(0.90)
                ?: transformWord(word)
        } else if (words.size == 2) {
            val phrase = "${words.first()} ${words.last()}"
            getRandomPhrase(dict, phrase).randomTake(0.6)
                ?: transformWord(words.last())
        } else {
            val lastWord = words.last()
            transformWord(lastWord)
                ?: getRandomPhrase(dict, lastWord)
        }.randomTake(0.8)

        if (response != null) messageCounter.randomReset(message.peerId)

        return response
    }

    private fun checkLinks(words: List<String>) = words.any { it.contains("http", ignoreCase = true) }

    private fun checkMentioning(words: List<String>) = words.any { it.contains("inyourpantsbot", ignoreCase = true) }

    private fun getRandomPhrase(dict: Phrases, phrase: String): String =
        if (phrase.isVerb()) {
            dict.randomPhrasesVerbs.random().format(phrase)
        } else {
            dict.randomPhrasesNouns.random().format(phrase)
        }

    private fun transformWord(word: String): String? = when (word.getOrNull(1)) {
        'э', 'е' -> "хуе" + word.substring(2)
        'а', 'я' -> "хуя" + word.substring(2)
        'и', 'ы' -> "хуи" + word.substring(2)
        'о', 'ё' -> "хуё" + word.substring(2)
        'у', 'ю' -> "хую" + word.substring(2)
        else -> null
    }

    private fun<T> T?.randomTake(factor: Double = 0.5): T? = takeIf { Random.nextDouble() < factor }
}
