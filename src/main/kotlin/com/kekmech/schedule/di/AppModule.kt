package com.kekmech.schedule.di

import com.google.gson.Gson
import com.kekmech.schedule.di.factories.GsonFactory
import com.kekmech.schedule.di.factories.HttpClientFactory
import com.kekmech.schedule.helpers.MessageCounter
import com.kekmech.schedule.helpers.ModuleProvider
import com.kekmech.schedule.repository.MainRepository
import com.kekmech.schedule.repository.sources.*
import io.ktor.client.*
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.bind
import java.util.*

class AppModule : ModuleProvider({
    single { GsonFactory.create() } bind Gson::class
    single { HttpClientFactory.create() } bind HttpClient::class
    single { Slf4JLoggerFactory.getInstance("BOT") } bind InternalLogger::class
    single { Locale.GERMAN } bind Locale::class

    single { MessageCounter() }
    single { PhrasesSource() }
    single { MainRepository(get(), get()) }
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}
