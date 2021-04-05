package com.kekmech.schedule

import com.kekmech.schedule.di.AppModule
import com.kekmech.schedule.helpers.modules
import com.kekmech.schedule.repository.MainRepository
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.util.*

val VK_GROUP_ID = System.getenv("VK_GROUP_ID")!!
val VK_BOT_SECRET_KEY = System.getenv("VK_BOT_SECRET_KEY")!!
val VK_ACCESS_KEY = System.getenv("VK_ACCESS_KEY")!!
val VK_CONFIRMATION_CODE = System.getenv("VK_CONFIRMATION_CODE")!!

val mainRepository by inject(MainRepository::class.java)
val httpClient by inject(HttpClient::class.java)

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("UNUSED")
fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging) {
        level = Level.TRACE
        callIdMdc("requestId")
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
        }
    }
    install(StatusPages) {
        exception<ExternalException> { cause ->
            call.respond(HttpStatusCode.ServiceUnavailable, cause.message.orEmpty())
        }
        exception<LogicException> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.orEmpty())
        }
        exception<ValidationException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.orEmpty())
        }
        exception<Exception> { cause ->
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            cause.printStackTrace(pw)
            call.respond(HttpStatusCode.InternalServerError, cause.message + "\n" + sw.toString())
        }
    }
    install(Koin) {
        printLogger()
        modules(AppModule())
    }
    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
        header("REQUEST_ID")
    }
    install(MicrometerMetrics) {
        registry = SimpleMeterRegistry()
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .percentiles(0.9, 0.99)
            .build()
    }
}
