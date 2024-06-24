package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.Port
import com.mattbobambrose.prisoner.common.PortNumber
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.getTimestamp
import com.mattbobambrose.prisoner.game_server.TransportType
import java.util.concurrent.CountDownLatch

object PlayerDSL {
    class GameServerContext(val gameServer: GameServer) {
        var concurrentMatches
            get() = gameServer.concurrentMatches
            set(value) {
                gameServer.concurrentMatches = value
            }
        val onBeginLambdas = mutableListOf<(GameServer) -> Unit>()
        val onEndLambdas = mutableListOf<(GameServer) -> Unit>()
        val competitionLamdas = mutableListOf<(GameServer) -> Unit>()
        val configLambdas = mutableListOf<(GameServerContext) -> Unit>()
    }

    class CompetitionContext(val competition: Competition) {
        val onBeginLambdas = mutableListOf<(Competition) -> Unit>()
        val onEndLambdas = mutableListOf<(Competition) -> Unit>()
        val playerLambdas = mutableListOf<() -> Unit>()
    }

    fun gameServer(transportType: TransportType, block: GameServerContext.() -> Unit) {
        with(GameServer(transportType)) {
            startGameServer()
            gameServerContext.apply {
                apply(block)
                onBeginLambdas.forEach { it(this@with) }
                configLambdas.forEach { it(gameServerContext) }
                // Create competitions
                competitionLamdas.forEach { it(this@with) }
                competitionMap.forEach { (_, competition) ->
                    competition.completionLatch.await()
                }
                competitionMap.keys.toList().forEach { competitionId ->
                    competitionMap.remove(competitionId)
                        ?: error("Error removing competition: $competitionId")
                }
                onEndLambdas.forEach { it(this@with) }
            }
            stopGameServer()
        }
    }

    fun GameServerContext.config(block: GameServerContext.() -> Unit) {
        configLambdas += block
    }

    fun GameServerContext.competition(
        name: String,
        block: CompetitionContext.() -> Unit
    ) {
        competitionLamdas += {
            val competitionId = CompetitionId("$name-${getTimestamp()}")
            val competition = Competition(gameServer, competitionId, CountDownLatch(1))
            gameServer.competitionMap[competitionId] = competition
            competition.competitionContext
                .apply(block)
                .onBeginLambdas.forEach { it(competition) }
            competition.createPlayerServers(gameServer)
            competition.competitionContext.playerLambdas.forEach { it() }
            val participants =
                competition.participantMap[competitionId] ?: error("No strategies found")
            require(participants.size > 1) { "Competition must have at least 2 strategies" }
            competition.registerPlayers()
            competition.triggerGameStart()
        }
    }

    fun GameServerContext.onBegin(block: (GameServer) -> Unit) {
        onBeginLambdas += block
    }

    fun GameServerContext.onEnd(block: (GameServer) -> Unit) {
        onEndLambdas += block
    }

    fun CompetitionContext.rules(block: Rules.() -> Unit) {
        competition.rules = Rules().apply(block)
    }

    fun CompetitionContext.player(username: String, block: Player.() -> Unit) {
        playerLambdas += {
            val port: Port = Port.nextAvailablePort()
            competition.playerMap[PortNumber(port.portNumber)] =
                Player(competition, Username(username), port).apply(block)
        }
    }

    fun CompetitionContext.onBegin(block: (Competition) -> Unit) {
        onBeginLambdas += block
    }

    fun CompetitionContext.onEnd(block: (Competition) -> Unit) {
        onEndLambdas += block
    }
}