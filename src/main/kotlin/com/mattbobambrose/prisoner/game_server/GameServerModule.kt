package com.mattbobambrose.prisoner.game_server

import GameServer
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
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
    val debug = environment.config
        .propertyOrNull("ktor.deployment.debug")?.getString()?.toBoolean() ?: false
    routing {
        post("/$REGISTER") {
            println("Debug: $debug")
            val participant = call.receive<GameParticipant>()
            println("Registered: $participant")
            GameServer.tournamentRequests.send(participant)
            call.respondText("Registered")
        }
        get("/") {
            call.respondText("Hello World1!")
        }
    }
}