package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyResponse
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.participantMap
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.strategyMap
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.playerServerRouting() {
    routing {
        get("/$STRATEGYFQNS") {
            val gameId = call.request.queryParameters["gameId"] ?: error("Missing gameId")
            val username = call.request.queryParameters["username"] ?: error("Missing username")
            val response = participantMap
                .filter { it.key.id == gameId }
                .map { participants ->
                    participants.value
                        .filter { it.username.name == username }
                        .map { it.fqn }
                }
                .flatten()
            println("Response: $response")
            call.respond(response)
        }

        post("/$STRATEGY/{fqn}") {
            val fqn = StrategyFqn(call.parameters["fqn"] ?: error("Invalid strategy"))
            val strategy = strategyMap[fqn] ?: error("Invalid fqn: $fqn")
            val args = call.receive<HttpObjects.StrategyArgs>()
            val decision = with(args) {
                strategy.chooseOption(roundNumber, opponentInfo.fqn.name, myMoves, opponentMoves)
            }
            call.respond(StrategyResponse(decision))
        }
    }
}