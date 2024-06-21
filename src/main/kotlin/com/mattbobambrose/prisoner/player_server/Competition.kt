package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.Utils
import com.mattbobambrose.prisoner.common.Utils.createHttpClient
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.player_server.PlayerDSL.CompetitionContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch

class Competition(
    val gameServer: GameServer,
    val competitionId: CompetitionId = CompetitionId(Utils.getRandomString(10)),
    val completionLatch: CountDownLatch
) {
    val strategies = mutableListOf<StrategyGroup>()
    var rules: Rules = Rules()
    val playerServers = mutableListOf<PlayerServer>()
    val competitionContext = CompetitionContext(this)

    init {
        PlayerServer.participantMap.putIfAbsent(competitionId, mutableListOf())
    }

    fun start() {
        strategies.forEach {
            val playerServer = PlayerServer(it.portNumber)
            playerServers += playerServer
            playerServer.startServer()
            val url = "http://localhost:${it.portNumber}"
            PlayerServer.register(it.username, competitionId, url, rules)
        }
        triggerGameStart()
    }

    private fun triggerGameStart() {
        // This will not wait for game completion
        createHttpClient().use { client ->
            runBlocking {
                client.get("http://localhost:$GAME_SERVER_PORT/$GO?$COMPETITION_ID=${competitionId.id.encode()}")
                logger.info { "Competition ${competitionId.id} started" }
            }
        }
    }

    fun onCompletion() {
        playerServers.forEach { it.stopServer() }
        strategies.forEach { it.port.setAvailable() }
        completionLatch.countDown()
        competitionContext.onEndLambdas.forEach { it(this) }
        logger.info { "Competition ${competitionId.id} completed" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}