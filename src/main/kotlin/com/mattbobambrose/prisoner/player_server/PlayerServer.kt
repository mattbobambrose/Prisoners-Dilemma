package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.GameId
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking

class PlayerServer(portNumber: Int) {
    private val server = embeddedServer(
        Netty,
        port = portNumber,
        host = "0.0.0.0",
        module = Application::playerModule
    )

    fun startServer() {
        server.start(wait = false)
    }

    fun stopServer() {
        server.stop(1000, 1000)
    }

    companion object {
        val competitionMap = mutableMapOf<GameId, MutableList<GameParticipant>>()
        val strategyMap = mutableMapOf<StrategyFqn, GameStrategy>()

        fun register(username: Username, gameId: GameId, url: String, rules: Rules) {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:$GAME_SERVER_PORT/$REGISTER") {
                        setJsonBody(
                            GameRequest(gameId, username, url, rules)
                        )
                    }
                }
            }
        }
    }
}