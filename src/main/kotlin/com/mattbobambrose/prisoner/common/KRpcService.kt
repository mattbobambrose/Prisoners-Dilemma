package com.mattbobambrose.prisoner.common

import com.mattbobambrose.prisoner.common.HttpObjects.StrategyInfo
import kotlinx.rpc.RPC

interface KRpcService : RPC {
    suspend fun requestDecision(
        competitionId: CompetitionId,
        info: StrategyInfo,
        opponentInfo: StrategyInfo,
        round: Int,
        myHistory: List<Decision>,
        opponentHistory: List<Decision>
    ): Decision

    suspend fun getStrategyFqnList(
        competitionId: CompetitionId,
        gameRequest: HttpObjects.GameRequest
    ): List<StrategyFqn>
}