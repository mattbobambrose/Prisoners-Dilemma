package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.EndpointNames.PARTICIPANTS
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.player_server.Game.Companion.strategyList
import com.mattbobambrose.prisoner.player_server.Game.Companion.strategyMap
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

fun Application.playerModule() {
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

    routing {
        get("/$PARTICIPANTS") {
            call.respond(strategyList.map { it.fqn })
        }
        post("/$STRATEGY/{fqn}") {
            val fqn = StrategyFqn(call.parameters["fqn"] ?: error("Invalid strategy"))
            val strategy = strategyMap[fqn] ?: error("Invalid fqn: $fqn")
            val args = call.receive<HttpObjects.StrategyArgs>()
            val decision = with(args) {
                strategy.chooseOption(roundNumber, opponentFqn.fqn, myMoves, opponentMoves)
            }
            call.respond(HttpObjects.StrategyResponse(decision))
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }
        get("matt.html") {
            call.respondHtml { // HTML
                body {
                    h1 { +"Sample HTML Page" }
                    p {
                        +"This is a sample HTML page"
                    }
                }
            }
        }
    }
}