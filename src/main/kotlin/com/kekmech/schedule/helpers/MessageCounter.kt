package com.kekmech.schedule.helpers

import com.kekmech.schedule.dto.Counter
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class MessageCounter {

    private val counters = ConcurrentHashMap<Int, Counter>()

    fun incrementCounterWith(peerId: Int) {
        val counter = counters[peerId] ?: Counter()
        counters[peerId] = counter.copy(
            skippedMessagesCount = counter.skippedMessagesCount - 1,
            lastAnswerDate = System.currentTimeMillis()
        )
    }

    fun isTimeForAnswer(peerId: Int): Boolean = (counters[peerId]?.skippedMessagesCount ?: 0) <= 0

    fun randomReset(peerId: Int) {
        val counter = counters[peerId] ?: Counter()
        counters[peerId] = counter.copy(
            skippedMessagesCount = 5 + Random.nextInt(9),
            lastAnswerDate = System.currentTimeMillis()
        )
    }
}