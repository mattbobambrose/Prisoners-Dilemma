package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.EndpointNames.KRPC_DECISION
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.KRpcService
import com.mattbobambrose.prisoner.player_server.PlayerServer.Companion.strategyMap
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h3
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import kotlin.coroutines.CoroutineContext

class KRpcServiceImpl(override val coroutineContext: CoroutineContext) : KRpcService {
    override suspend fun requestDecision(
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int,
        myHistory: List<Decision>,
        opponentHistory: List<Decision>
    ): Decision {
        val strategy = strategyMap[info.fqn] ?: error("Invalid fqn: ${info.fqn}")
        return strategy.chooseOption(
            round,
            opponentInfo.fqn.name,
            myHistory,
            opponentHistory
        )
    }
}

fun Application.gameServerModule() {
    install(RPC)
    routing {
        rpc("/$KRPC_DECISION") {
            rpcConfig {
                serialization {
                    json()
                }
            }

            registerService<KRpcService> { ctx -> KRpcServiceImpl(ctx) }
        }
    }

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
