package com.mattbobambrose.prisoner.game_server

import com.mattbobambrose.prisoner.common.CompetitionId
import com.mattbobambrose.prisoner.common.Decision
import com.mattbobambrose.prisoner.common.HttpObjects
import com.mattbobambrose.prisoner.common.HttpObjects.GameRequest
import com.mattbobambrose.prisoner.common.StrategyFqn

interface CallTransport {
    suspend fun requestDecision(
        match: Match,
        info: HttpObjects.StrategyInfo,
        opponentInfo: HttpObjects.StrategyInfo,
        round: Int
    ): Decision

    suspend fun getStrategyFqnList(
        competitionId: CompetitionId,
        gameRequest: GameRequest
    ): List<StrategyFqn>
}