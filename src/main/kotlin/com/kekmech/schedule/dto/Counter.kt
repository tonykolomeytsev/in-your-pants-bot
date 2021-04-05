package com.kekmech.schedule.dto

data class Counter(
    val skippedMessagesCount: Int = 0,
    val lastAnswerDate: Long = 0
)