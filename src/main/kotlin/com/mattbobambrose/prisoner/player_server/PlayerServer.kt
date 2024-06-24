package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.REGISTER
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.PortNumber
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.setJsonBody
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking

class PlayerServer(val gameServer: GameServer, val portNumber: Int) {
    val transportType = gameServer.transportType
    private lateinit var playerHttpServer: NettyApplicationEngine

    fun startPlayerServer() {
        if (transportType.requiresHttp) {
            playerHttpServer = embeddedServer(
                io.ktor.server.netty.Netty,
                port = portNumber,
                host = "0.0.0.0",
                module = { playerModule(gameServer) }
            ).also { logger.info { "Player server created on port $portNumber" } }
            playerHttpServer.start(wait = false)
        }
    }

    fun stopPlayerServer() {
        if (transportType.requiresHttp) {
            playerHttpServer.stop(1000, 1000)
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}

        fun register(
            username: Username,
            competitionId: CompetitionId,
            url: String,
            portNumber: Int,
            rules: Rules
        ) {
            HttpClient(io.ktor.client.engine.cio.CIO) {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }.use { client ->
                runBlocking {
                    client.post("http://localhost:$GAME_SERVER_PORT/$REGISTER") {
                        setJsonBody(
                            GameRequest(competitionId, username, url, PortNumber(portNumber), rules)
                        )
                    }
                }
            }
        }
    }
}