package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo

class LocalTransport(val match: Match) : CallTransport {
    override suspend fun requestDecision(
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
}