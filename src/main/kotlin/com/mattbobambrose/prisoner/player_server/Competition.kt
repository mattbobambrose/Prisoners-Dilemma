package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Constants.COMPETITION_ID
import com.mattbobambrose.prisoner.common.Constants.GAME_SERVER_PORT
import com.mattbobambrose.prisoner.common.EndpointNames.GO
import com.mattbobambrose.prisoner.common.HttpObjects.PlayerDTO
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.PortNumber
import com.mattbobambrose.prisoner.common.StrategyFqn
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils
import com.mattbobambrose.prisoner.common.Utils.createHttpClient
import com.mattbobambrose.prisoner.common.Utils.encode
import com.mattbobambrose.prisoner.player_server.PlayerDSL.CompetitionContext
import com.mattbobambrose.prisoner.strategy.GameStrategy
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch

class Competition(
    val gameServer: GameServer,
    val competitionId: CompetitionId = CompetitionId(Utils.getRandomString(10)),
    val completionLatch: CountDownLatch
) {
    val playerMap = mutableMapOf<PortNumber, Player>()
    var rules: Rules = Rules()
    val playerServers = mutableListOf<PlayerServer>()
    val competitionContext = CompetitionContext(this)
    val participantMap = mutableMapOf<CompetitionId, MutableList<PlayerDTO>>()
    val strategyMap = mutableMapOf<StrategyFqn, GameStrategy>()

    init {
        participantMap.putIfAbsent(competitionId, mutableListOf())
    }

    fun createPlayerServers(gameServer: GameServer) {
        playerMap.forEach { portNumber, player ->
            val playerServer = PlayerServer(gameServer, portNumber.number)
            playerServers += playerServer
            playerServer.startPlayerServer()
        }
    }

    fun registerPlayers() {
        playerMap.forEach { portNumber, player ->
            val url = "http://localhost:${portNumber}"
            PlayerServer.register(player.username, competitionId, url, portNumber.number, rules)
        }
    }

    fun triggerGameStart() {
        // This will not wait for game completion
        createHttpClient().use { client ->
            runBlocking {
                client.get("http://localhost:$GAME_SERVER_PORT/$GO?$COMPETITION_ID=${competitionId.id.encode()}")
                logger.info { "Competition ${competitionId.id} started" }
            }
        }
    }

    fun addStrategy(username: Username, strategy: GameStrategy) {
        participantMap[competitionId]?.add(PlayerDTO(username, competitionId, strategy.fqn))
        strategyMap[strategy.fqn] = strategy
    }

    fun getStrategyFqnList(username: Username): List<StrategyFqn> {
        return participantMap
            .filter { it.key.id == competitionId.id }
            .map { participants ->
                participants.value
                    .filter { it.username.name == username.name }
                    .map { it.fqn }
            }
            .flatten()
    }

    fun onCompletion() {
        playerServers.forEach { it.stopPlayerServer() }
        playerMap.values.forEach { it.port.setAvailable() }
        completionLatch.countDown()
        competitionContext.onEndLambdas.forEach { it(this) }
        logger.info { "Competition ${competitionId.id} completed" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}