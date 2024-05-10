package com.mattbobambrose.prisoner.game_server

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import kotlinx.html.body
import kotlinx.html.h3

fun Application.gameServerModule() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
//            call.respondText(text = "404: I'm sorry that page is not present", status = status)
            call.respondHtml(status = status) {
                body {
                    h3 { +"404: I'm sorry that page is not present" }
                }
            }
        }
    }

    install(Resources)
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        filter { false }
    }

    gameServerRouting()

}
