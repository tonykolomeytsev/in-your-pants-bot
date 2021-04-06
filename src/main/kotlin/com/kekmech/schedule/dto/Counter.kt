package com.kekmech.schedule.dto

data class Counter(
    val skippedMessagesCount: Int = 3,
    val lastAnswerDate: Long = 0
)