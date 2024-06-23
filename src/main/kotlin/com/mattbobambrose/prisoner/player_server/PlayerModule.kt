package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.game_server.TransportType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import kotlinx.html.body
import kotlinx.html.h3

fun Application.playerModule(gameServer: GameServer) {
    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        filter {
            false
        }
    }

    install(StatusPages) {
        status(io.ktor.http.HttpStatusCode.NotFound) { call, status ->
            call.respondHtml(status = status) {
                body {
                    h3 { +"404: I'm sorry that page is not present" }
                }
            }
        }
    }

    when (gameServer.transportType) {
        TransportType.REST -> configureRestEndpoints(gameServer)
        TransportType.KRPC -> configureKrpcEndpoints(gameServer)
        else -> error("Unsupported transport type")
    }
}