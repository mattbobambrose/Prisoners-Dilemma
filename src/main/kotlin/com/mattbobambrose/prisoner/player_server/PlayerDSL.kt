package com.mattbobambrose.prisoner.player_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.HttpObjects.Rules
import com.mattbobambrose.prisoner.common.Port
import com.mattbobambrose.prisoner.common.Username
import com.mattbobambrose.prisoner.common.Utils.getTimestamp
import java.util.concurrent.CountDownLatch

object PlayerDSL {
    class GameServerContext(val gameServer: GameServer) {
        val onBeginLambdas = mutableListOf<(GameServer) -> Unit>()
        val onEndLambdas = mutableListOf<(GameServer) -> Unit>()
    }

    class CompetitionContext(val competition: Competition) {
        val onBeginLambdas = mutableListOf<(Competition) -> Unit>()
        val onEndLambdas = mutableListOf<(Competition) -> Unit>()
    }

    fun gameServer(block: GameServerContext.() -> Unit) {
        with(GameServer()) {
            startServer()
            gameServerContext
                .apply(block)
                .onBeginLambdas.forEach { it(this) }
            competitionMap.forEach { (_, competition) ->
                competition.completionLatch.await()
            }
            competitionMap.keys.toList().forEach { competitionId ->
                competitionMap.remove(competitionId)
                    ?: error("Error removing competition: $competitionId")
            }
            gameServerContext.onEndLambdas.forEach { it(this) }
            stopServer()
        }
    }

    fun GameServerContext.competition(
        name: String,
        block: CompetitionContext.() -> Unit
    ) {
        val competitionId = CompetitionId("$name-${getTimestamp()}")
        val competition = Competition(gameServer, competitionId, CountDownLatch(1))
        gameServer.competitionMap[competitionId] = competition
        competition.competitionContext
            .apply(block)
            .onBeginLambdas.forEach { it(competition) }
        competition.start(gameServer)
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
        val port: Port = Port.nextAvailablePort()
        competition.players += Player(competition, Username(username), port).apply(block)
    }

    fun CompetitionContext.onBegin(block: (Competition) -> Unit) {
        onBeginLambdas += block
    }

    fun CompetitionContext.onEnd(block: (Competition) -> Unit) {
        onEndLambdas += block
    }
}