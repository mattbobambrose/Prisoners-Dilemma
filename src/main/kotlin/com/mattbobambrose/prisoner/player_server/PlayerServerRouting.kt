package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Constants.FQN
import com.mattbobambrose.prisoner.common.EndpointNames.KRPC_DECISION
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGY
import com.mattbobambrose.prisoner.common.EndpointNames.STRATEGYFQNS
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyArgs
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyResponse
import com.mattbobambrose.prisoner.common.KRpcService
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.decode
import com.mattbobambrose.prisoner.game_server.KRpcServiceImpl
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc

fun Application.configureKrpcEndpoints(gameServer: GameServer) {
    PlayerServer.logger.info { "Configuring KRPC endpoints" }
    install(RPC)
    routing {
        rpc("/$KRPC_DECISION") {
            rpcConfig {
                serialization {
                    json()
                }
            }
            registerService<KRpcService> { ctx -> KRpcServiceImpl(gameServer.competitionMap, ctx) }
        }
    }
}

fun Application.configureRestEndpoints(gameServer: GameServer) {
    PlayerServer.logger.info { "Configuring REST endpoints" }
    routing {
        get("/$STRATEGYFQNS") {
            val competitionId =
                call.request.queryParameters[COMPETITION_ID] ?: error("Missing $COMPETITION_ID")
            val username = call.request.queryParameters["username"] ?: error("Missing username")
            val competition = gameServer.competitionMap[CompetitionId(competitionId)]
                ?: error("Invalid competition id")
            val response = competition.getStrategyFqnList(Username(username))
            GameServer.logger.info { "Response: $response" }
            call.respond(response)
        }

        post("/$STRATEGY/{competitionId}/{fqn}") {
            val competitionId =
                CompetitionId(
                    call.parameters[COMPETITION_ID]?.decode()
                        ?: error("Missing competition id: $COMPETITION_ID")
                )
            val fqn = StrategyFqn(call.parameters[FQN]?.decode() ?: error("Invalid strategy"))
            val competition =
                gameServer.competitionMap[competitionId] ?: error("Invalid competition id")
            val strategy = competition.strategyMap[fqn] ?: error("Invalid fqn: $fqn")
            val args = call.receive<StrategyArgs>()
            val decision = with(args) {
                strategy.chooseOption(roundNumber, opponentInfo.fqn.name, myMoves, opponentMoves)
            }
            call.respond(StrategyResponse(decision))
        }
    }
}