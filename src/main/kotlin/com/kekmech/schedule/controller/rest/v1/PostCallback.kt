package com.kekmech.schedule.controller.rest.v1

import com.google.gson.Gson
import com.kekmech.schedule.*
import com.kekmech.schedule.dto.PayloadType
import com.kekmech.schedule.dto.PostCallbackPayload
import com.kekmech.schedule.dto.PostCallbackPayloadMessage
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.netty.util.internal.logging.InternalLogger
import org.koin.java.KoinJavaComponent.inject
import java.nio.charset.Charset


@Location("/v1/callback")
class PostCallback

private val gson by inject(Gson::class.java)
private val log by inject(InternalLogger::class.java)

fun Route.postCallbackV1() {
    post<PostCallback> {
        val content = call.receiveTextWithCorrectEncoding()
        val request = gson.fromJson(content, PostCallbackPayload::class.java)
        check(request.groupId == VK_GROUP_ID)
        check(request.secret == VK_BOT_SECRET_KEY)
        log.info(content)
        when (request.type) {
            PayloadType.CONFIRMATION -> {
                call.respond(HttpStatusCode.OK, VK_CONFIRMATION_CODE)
            }
            PayloadType.MESSAGE_NEW -> {
                val message = gson.fromJson(content, PostCallbackPayloadMessage::class.java).payload.message
                val answer = mainRepository.getPhrase(message)

                if (answer != null) {
                    val queries = mapOf(
                        "message" to answer,
                        "peer_id" to message.peerId,
                        "access_token" to VK_ACCESS_KEY,
                        "v" to "5.130",
                        "random_id" to 0
                    )
                        .map { (key, value) -> "$key=$value" }
                        .joinToString(separator = "&") { it }

                    httpClient.getUnsafe<Any>("api.vk.com/method/messages.send/?$queries")
                }

                call.respond(HttpStatusCode.OK, "ok")
            }
        }
    }
}

private suspend fun ApplicationCall.receiveTextWithCorrectEncoding(): String {
    fun ContentType.defaultCharset(): Charset = when (this) {
        ContentType.Application.Json -> Charsets.UTF_8
        else -> Charsets.ISO_8859_1
    }

    val contentType = request.contentType()
    val suitableCharset = contentType.charset() ?: contentType.defaultCharset()
    return receiveStream().bufferedReader(charset = suitableCharset).readText()
}

private suspend inline fun <reified T> HttpClient.getUnsafe(unsafeUrl: String): T = get {
    url.protocol = URLProtocol.HTTPS
    url.host = ""
    url.encodedPath = unsafeUrl
}