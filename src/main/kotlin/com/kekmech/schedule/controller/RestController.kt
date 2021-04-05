package com.kekmech.schedule.controller

import com.kekmech.schedule.controller.rest.v1.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

@Suppress("UNUSED")
fun Application.restModule() {
    install(Locations)

    routing {
        // System
        healthCheck()
        // V1
        postCallbackV1()
    }
}
