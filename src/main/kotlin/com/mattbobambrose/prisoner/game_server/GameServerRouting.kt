package com.mattbobambrose.prisoner.game_server

import GameServer
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.gameServerRouting() {
    routing {
        val debug = this@gameServerRouting.environment.config
            .propertyOrNull("ktor.deployment.debug")?.getString()?.toBoolean() ?: false

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