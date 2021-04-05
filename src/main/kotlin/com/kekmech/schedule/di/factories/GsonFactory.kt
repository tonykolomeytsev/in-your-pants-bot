package com.kekmech.schedule.di.factories

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.text.DateFormat

object GsonFactory {
    fun create(): Gson = GsonBuilder().apply {
        setDateFormat(DateFormat.LONG)
    }.create()
}
