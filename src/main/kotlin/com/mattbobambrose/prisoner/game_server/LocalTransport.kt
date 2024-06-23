package com.mattbobambrose.prisoner.game_server

import GameServer
import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import com.mattbobambrose.prisoner.common.StrategyFqn

class LocalTransport(val gameServer: GameServer) : CallTransport {
    override suspend fun requestDecision(
        match: Match,
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int
    ): Decision {
        val competition = match.generation.game.gameServer.competitionMap[match.competitionId]
            ?: error("Invalid competition id")
        val strategy = competition.strategyMap[info.fqn] ?: error("Invalid fqn: ${info.fqn}")
        return strategy.chooseOption(
            round,
            opponentInfo.fqn.name,
            match.makeHistory(info),
            match.makeHistory(opponentInfo)
        )
    }

    override suspend fun getStrategyFqnList(
        competitionId: CompetitionId,
        gameRequest: GameRequest
    ): List<StrategyFqn> {
        return gameServer.competitionMap[competitionId]?.getStrategyFqnList(gameRequest.username)
            ?: error("Invalid competition id: $competitionId")
    }
}