package com.mattbobambrose.prisoner.player_server

import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.HttpObjects.GameParticipant
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking

class PlayerServer(val portNumber: Int) {
    private val playerHttpServer = embeddedServer(
        Netty,
        port = portNumber,
        host = "0.0.0.0",
        module = Application::playerModule
    ).also { logger.info { "Player server created on port $portNumber" } }

    fun startServer() {
        logger.info { "Starting player server on port $portNumber" }
        playerHttpServer.start(wait = false)
    }

    fun stopServer() {
        playerHttpServer.stop(1000, 1000)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        val participantMap = mutableMapOf<CompetitionId, MutableList<GameParticipant>>()
        val strategyMap = mutableMapOf<StrategyFqn, GameStrategy>()

        fun register(username: Username, competitionId: CompetitionId, url: String, rules: Rules) {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:$GAME_SERVER_PORT/$REGISTER") {
                        setJsonBody(
                            GameRequest(competitionId, username, url, rules)
                        )
                    }
                }
            }
        }
    }
}